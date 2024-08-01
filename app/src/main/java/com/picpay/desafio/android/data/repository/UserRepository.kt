package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.common.configs.PicPayService
import com.picpay.desafio.android.common.utils.PreferencesManager
import com.picpay.desafio.android.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(
    private val service: PicPayService,
    private val preferencesManager: PreferencesManager
) {

    fun getUsers(onResult: (Result<List<User>>) -> Unit) {

        val cachedUsers = preferencesManager.getCacheUsers()
        if (!cachedUsers.isNullOrEmpty()) {
            onResult(Result.success(cachedUsers))
            return
        }

        service.getUsers().enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                onResult(Result.failure(t))
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    response.body()?.let { users ->
                        preferencesManager.saveCacheUsers(users)
                        onResult(Result.success(users))
                    } ?: run {
                        onResult(Result.failure(Exception("Response body is null")))
                    }
                } else {
                    onResult(Result.failure(Exception("Error: ${response.code()} - ${response.message()}")))
                }
            }
        })
    }
}