package com.picpay.desafio.android.common.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.picpay.desafio.android.data.model.User

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCacheUsers(users: List<User>) {
        val json = gson.toJson(users)
        sharedPreferences.edit().putString(USERS_KEY, json).apply()
    }

    fun getCacheUsers(): List<User>? {
        val json = sharedPreferences.getString(USERS_KEY, null)
        return json?.let {
            gson.fromJson(it, Array<User>::class.java).toList()
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "com.picpay.desafio.android.PREFERENCES"
        private const val USERS_KEY = "USERS"
    }
}