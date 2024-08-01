package com.picpay.desafio.android.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import com.picpay.desafio.android.common.configs.PicPayService
import com.picpay.desafio.android.common.utils.PreferencesManager
import com.picpay.desafio.android.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.test.withTestContext

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class UserRepositoryInstrumentedTest {

    private lateinit var repository: UserRepository
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PicPayService
    private lateinit var sharedPreferencesManager: PreferencesManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sharedPreferencesManager = PreferencesManager(context)

        mockWebServer = MockWebServer()
        mockWebServer.start()

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicPayService::class.java)

        repository = UserRepository(service, sharedPreferencesManager)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getUsers_shouldReturnUsers() = withTestContext {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[{\"id\":\"1\",\"name\":\"John Doe\",\"username\":\"johndoe\",\"img\":\"image\"}]")
        mockWebServer.enqueue(mockResponse)

        repository.getUsers { result ->
            assert(result.isSuccess)
            val users = result.getOrNull()
            assert(users?.size == 1)
            assert(users?.get(0)?.name == "John Doe")
        }
    }

    @Test
    fun getUsers_shouldHandleApiError() = withTestContext {
        val mockResponse = MockResponse().setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        repository.getUsers { result ->
            assert(result.isFailure)
        }
    }
}