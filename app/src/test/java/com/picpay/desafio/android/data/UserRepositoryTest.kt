package com.picpay.desafio.android.data

import com.picpay.desafio.android.data.model.User
import com.picpay.desafio.android.common.configs.PicPayService
import com.picpay.desafio.android.common.utils.PreferencesManager
import com.picpay.desafio.android.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    @Mock
    private lateinit var service: PicPayService

    @Mock
    private lateinit var sharedPreferencesManager: PreferencesManager

    private lateinit var repository: UserRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = UserRepository(service, sharedPreferencesManager)
    }

    @Test
    fun `getUsers should return cached users when available`() = runBlockingTest {
        val cachedUsers = listOf(User("1", "Diego Dantas", 1, "Diego"))
        `when`(sharedPreferencesManager.getCacheUsers()).thenReturn(cachedUsers)

        repository.getUsers { result ->
            assertTrue(result.isSuccess)
            assertEquals(cachedUsers, result.getOrNull())
        }

        verify(service, never()).getUsers()
    }

    @Test
    fun `getUsers should fetch from API when cache is empty`() = runBlockingTest {
        `when`(sharedPreferencesManager.getCacheUsers()).thenReturn(emptyList())

        val call = mock(Call::class.java) as Call<List<User>>
        `when`(service.getUsers()).thenReturn(call)

        repository.getUsers { result ->
            assertTrue(result.isFailure)
        }

        verify(call).enqueue(any())
    }

    @Test
    fun `getUsers should cache users on successful API response`() = runBlockingTest {
        `when`(sharedPreferencesManager.getCacheUsers()).thenReturn(emptyList())

        val call = mock(Call::class.java) as Call<List<User>>
        `when`(service.getUsers()).thenReturn(call)

        val users = listOf(User("1", "Diego Dantas", 1, "Diego"))
        doAnswer {
            val callback: Callback<List<User>> = it.getArgument(0)
            callback.onResponse(call, Response.success(users))
            null
        }.`when`(call).enqueue(any())

        repository.getUsers { result ->
            assertTrue(result.isSuccess)
            assertEquals(users, result.getOrNull())
        }

        verify(sharedPreferencesManager).saveCacheUsers(users)
    }

    @Test
    fun `getUsers should handle API failure`() = runBlockingTest {
        `when`(sharedPreferencesManager.getCacheUsers()).thenReturn(emptyList())

        val call = mock(Call::class.java) as Call<List<User>>
        `when`(service.getUsers()).thenReturn(call)

        val exception = RuntimeException("Network error")
        doAnswer {
            val callback: Callback<List<User>> = it.getArgument(0)
            callback.onFailure(call, exception)
            null
        }.`when`(call).enqueue(any())

        repository.getUsers { result ->
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }
    }
}