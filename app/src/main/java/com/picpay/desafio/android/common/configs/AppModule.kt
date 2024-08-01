package com.picpay.desafio.android.common.configs

import com.picpay.desafio.android.BuildConfig
import com.picpay.desafio.android.common.utils.PreferencesManager
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.ui.viewModel.UserViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.BUILD_TYPE == "debug") {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://609a908e0f5a13001721b74e.mockapi.io/picpay/api/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicPayService::class.java)
    }
}

val repositoryModule = module {
    single { UserRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { UserViewModel(get()) }
}

val preferencesModule = module {
    single { PreferencesManager(get()) }
}

val appModule = listOf(networkModule, repositoryModule, viewModelModule, preferencesModule)