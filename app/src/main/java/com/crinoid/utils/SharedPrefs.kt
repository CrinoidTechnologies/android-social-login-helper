package com.crinoid.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class SharedPrefs(context: Context) {

    private val fileName: String = context.packageName + ".prefFile"
    private val preferences: SharedPreferences
    val localData: LocalData

    init {
        preferences = context.getSharedPreferences(this.fileName, 0)
        val data = readString(99)
        Log.d("TAG", "BaseCurrentSession: $data")
        if (data.length > 1) {
            this.localData = Gson().fromJson<LocalData>(data, LocalData::class.java)
        }else{
            this.localData = LocalData()
        }
    }

    fun writeString(key: Int, value: String) {
        this.preferences.edit().putString(key.toString(), value).apply()
    }

    private fun readString(key: Int): String {
        return this.preferences.getString(key.toString(), "")!!
    }

    fun saveDataLocally() {
        writeString(99,Gson().toJson(localData))
    }

    fun isLoggedIn(): Boolean {
        return localData?.instaUserBd!=null
    }
}