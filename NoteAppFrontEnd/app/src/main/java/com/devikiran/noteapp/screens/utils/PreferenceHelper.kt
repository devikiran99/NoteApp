package com.devikiran.noteapp.screens.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class PreferenceHelper @Inject constructor(context: Context) {


    private var prefs: SharedPreferences = context.getSharedPreferences(REFERENCE, Context.MODE_PRIVATE)


//    var accessToken: String
//        get() = prefs.getString(ACCESS_TOKEN, "") ?: ""
//        set(value) = prefs.edit { putString(ACCESS_TOKEN, value) }
//


    companion object{
        private const val REFERENCE = "REFERENCE"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"

    }
}