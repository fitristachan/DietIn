package com.dietinapp.retrofit.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dietinapp.retrofit.data.di.HistoryPagingInjection
import com.dietinapp.retrofit.data.repository.HistoriesPagingRepository
import com.dietinapp.retrofit.response.HistoryItem
import kotlinx.coroutines.flow.Flow

class HistoryPagingViewModel(
    private val historiesPagingRepository: HistoriesPagingRepository
): ViewModel() {
    fun getHistories(): Flow<PagingData<HistoryItem>> {
        return historiesPagingRepository.getHistories()
            .cachedIn(viewModelScope)
    }
}


class HistoryPagingViewModelFactory(
    private val historiesPagingRepository: HistoriesPagingRepository):
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryPagingViewModel::class.java)) {
            return HistoryPagingViewModel(historiesPagingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: HistoryPagingViewModelFactory? = null
        fun getInstance(context: Context): HistoryPagingViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HistoryPagingViewModelFactory(HistoryPagingInjection.provideRepository(context))
            }.also { instance = it }
    }
}