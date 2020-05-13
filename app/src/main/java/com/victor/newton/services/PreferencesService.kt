package com.victor.newton.services

import android.content.Context


class PreferencesService(private val context: Context) {

    private val preferencesFile: String = "NEWTON_PREFERENCES"

    fun getPreference(key: String) : String?{
        val sharedPreference =  context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
        return sharedPreference.getString(key,null)
    }

    fun savePreference(key: String, value :String){
        val sharedPreference =  context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString(key,value)
        editor.apply()
    }

}
