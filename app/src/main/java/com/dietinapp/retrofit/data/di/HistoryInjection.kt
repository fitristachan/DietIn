package com.dietinapp.retrofit.data.di

import android.content.Context
import com.dietinapp.database.datastore.UserPreference
import com.dietinapp.database.datastore.dataStore
import com.dietinapp.retrofit.api.ApiConfig
import com.dietinapp.retrofit.data.repository.HistoryRepository
import kotlinx.coroutines.runBlocking

object HistoryInjection {
    fun provideRepository(context: Context): HistoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val token = runBlocking { pref.getToken() }
        val apiService = ApiConfig.getApiService(token)
        return HistoryRepository.getInstance(apiService)
    }
}