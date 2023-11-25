package com.dietin.data.preferences

import android.content.Context
import android.content.SharedPreferences

class OnboardingManager(context: Context) {
    private val onBoardingPreferences: SharedPreferences =
        context.getSharedPreferences("isOnBoardingCompleted", Context.MODE_PRIVATE)

    fun saveData(key: String, value: Boolean) {
        val editor = onBoardingPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getData(key: String, defaultValue: Boolean): Boolean {
        return onBoardingPreferences.getBoolean(key, defaultValue)
    }
}
