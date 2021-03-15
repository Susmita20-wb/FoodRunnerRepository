package com.internshala.foodrunner.util

import android.content.Context
import com.internshala.foodrunner.R

class SessionManager(context:Context) {
    var PRIVATE_MODE=0
    val PREF_NAME= R.string.preference_file_name.toString()

     val key_is_logged_in="IsLoggedIn"
    var pref=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
    var editor=pref.edit()

    fun setLogin(isLoggedIn:Boolean)
    {
        editor.putBoolean(key_is_logged_in,isLoggedIn)
        editor.apply()
    }
    fun isLoggedIn(): Boolean{
        return pref.getBoolean(key_is_logged_in,false)
    }

}