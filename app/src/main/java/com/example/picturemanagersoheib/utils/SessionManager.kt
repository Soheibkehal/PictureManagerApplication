package com.example.picturemanagersoheib.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.PictureManagerApplication
import com.example.picturemanagersoheib.R

class SessionManager {
    private val context : Context = PictureManagerApplication.getAppContext()
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.access_token), Context.MODE_PRIVATE)

    companion object {
        const val ACCESS_TOKEN = "user_token"
        const val USER_ID = "user_id"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthTokenAndUserId(token: String, userId: Int) {
        val editor = prefs.edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.putString(USER_ID, userId.toString())
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(ACCESS_TOKEN, null)
    }

    fun fetchUserId(): String? {
        return prefs.getString(USER_ID, null)
    }
}