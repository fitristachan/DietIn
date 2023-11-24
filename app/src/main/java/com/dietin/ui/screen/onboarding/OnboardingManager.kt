package com.dietin.ui.screen.onboarding

import android.content.Context
import android.content.SharedPreferences

class OnboardingManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("isOnBoardingCompleted", Context.MODE_PRIVATE)

    fun saveData(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getData(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}
