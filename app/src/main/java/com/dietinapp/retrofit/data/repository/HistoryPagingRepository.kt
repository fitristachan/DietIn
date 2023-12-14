package com.dietinapp.retrofit.data.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dietinapp.retrofit.api.ApiService
import com.dietinapp.retrofit.data.paging.HistoriesPagingDatabase
import com.dietinapp.retrofit.data.paging.HistoriesRemoteMediator
import com.dietinapp.retrofit.response.HistoryItem
import kotlinx.coroutines.flow.Flow

class HistoriesPagingRepository(
    private val historiesPagingDatabase: HistoriesPagingDatabase, private val apiService: ApiService
) {
    fun getHistories(foodName: String, createdAt: String, status: Boolean?): Flow<PagingData<HistoryItem>> {
        @OptIn(ExperimentalPagingApi::class) return Pager(config = PagingConfig(
            pageSize = 5
        ),
            remoteMediator = HistoriesRemoteMediator(historiesPagingDatabase, apiService, status),
            pagingSourceFactory = {
                historiesPagingDatabase.historiesPagingDao().getAllHistories(
                    foodName = foodName,
                    createdAt = createdAt)
            }).flow
    }

    companion object {
        @Volatile
        private var instance: HistoriesPagingRepository? = null
        fun getInstance(
            storiesPagingDatabase: HistoriesPagingDatabase, apiService: ApiService
        ): HistoriesPagingRepository = instance ?: synchronized(this) {
            instance ?: HistoriesPagingRepository(storiesPagingDatabase, apiService)
        }.also { instance = it }
    }

}
