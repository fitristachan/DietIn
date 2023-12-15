package com.dietinapp.retrofit.data.di

import android.content.Context
import com.dietinapp.database.datastore.UserPreference
import com.dietinapp.database.datastore.dataStore
import com.dietinapp.retrofit.api.ApiConfig
import com.dietinapp.retrofit.data.paging.HistoriesPagingDatabase
import com.dietinapp.retrofit.data.repository.HistoriesPagingRepository
import kotlinx.coroutines.runBlocking

object HistoryPagingInjection {
    fun provideRepository(context: Context): HistoriesPagingRepository {
        val database = HistoriesPagingDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val token = runBlocking { pref.getToken() }
        val apiService = ApiConfig.getApiService(token)
        return HistoriesPagingRepository.getInstance(database, apiService)
    }
}