package com.dietinapp.retrofit.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dietinapp.retrofit.data.di.HistoryInjection
import com.dietinapp.retrofit.data.repository.HistoryRepository
import com.dietinapp.retrofit.response.IngredientsItem
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class HistoryViewModel(
    private val historyRepository: HistoryRepository
): ViewModel() {
    val successMessage: StateFlow<String> = historyRepository.successMessage
    val errorMessage: StateFlow<String> = historyRepository.errorMessage
    val isLoading: StateFlow<Boolean> = historyRepository.isLoading


    fun addHistory(
        foodPhoto: File,
        foodName: String,
        lectineStatus: Boolean,
        ingredients: List<IngredientsItem>,
    ) = viewModelScope.launch { historyRepository.addHistory(foodPhoto, foodName, lectineStatus, ingredients) }

    fun getDetailHistory(historyId: String) = historyRepository.getDetailHistory(historyId)

    fun deleteHistory(historyId: String) = viewModelScope.launch {historyRepository.deleteHistory(historyId)}
}


class HistoryViewModelFactory(private val historyRepository: HistoryRepository):
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: HistoryViewModelFactory? = null
        fun getInstance(context: Context): HistoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HistoryViewModelFactory(HistoryInjection.provideRepository(context))
            }.also { instance = it }
    }
}