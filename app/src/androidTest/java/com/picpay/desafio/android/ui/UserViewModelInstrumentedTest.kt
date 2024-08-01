package com.picpay.desafio.android.ui

import android.content.Context
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import com.picpay.desafio.android.common.configs.PicPayService
import com.picpay.desafio.android.common.utils.PreferencesManager
import com.picpay.desafio.android.data.model.User
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.ui.viewModel.UserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.withTestContext
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class UserViewModelInstrumentedTest {

    @Mock
    private lateinit var observerUsers: Observer<List<User>>

    @Mock
    private lateinit var observerError: Observer<String>

    @Mock
    private lateinit var observerLoading: Observer<Boolean>

    private lateinit var viewModel: UserViewModel
    private lateinit var repository: UserRepository
    private lateinit var mockWebServer: MockWebServer
    private lateinit var sharedPreferencesManager: PreferencesManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val context = ApplicationProvider.getApplicationContext<Context>()
        sharedPreferencesManager = PreferencesManager(context)

        mockWebServer = MockWebServer()
        mockWebServer.start()

        val service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicPayService::class.java)

        repository = UserRepository(service, sharedPreferencesManager)
        viewModel = UserViewModel(repository)
        viewModel.users.observeForever(observerUsers)
        viewModel.isLoading.observeForever(observerLoading)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun fetchUsers_shouldPostLoadingAndSuccess() = withTestContext {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[{\"id\":\"1\",\"name\":\"John Doe\",\"username\":\"johndoe\",\"img\":\"image\"}]")
        mockWebServer.enqueue(mockResponse)

        viewModel.fetchUsers()

        assert(viewModel.isLoading.value == true)
        assert(viewModel.isLoading.value == false)
        val users = viewModel.users.value
        assert(users?.size == 1)
        assert(users?.get(0)?.name == "John Doe")
    }

    @Test
    fun fetchUsers_shouldPostLoadingAndError() = withTestContext {
        val mockResponse = MockResponse().setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        viewModel.fetchUsers()

        assert(viewModel.isLoading.value == true)
        assert(viewModel.isLoading.value == false)
        assert(viewModel.error.value != null)
    }
}