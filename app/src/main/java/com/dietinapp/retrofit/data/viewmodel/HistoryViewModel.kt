package com.dietinapp.retrofit.data.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dietinapp.retrofit.data.di.HistoryInjection
import com.dietinapp.retrofit.data.repository.HistoryRepository
import com.dietinapp.retrofit.response.IngredientsItem
import java.io.File

class HistoryViewModel(
    private val historyRepository: HistoryRepository
): ViewModel() {
    val successMessage: LiveData<String> = historyRepository.successMessage
    val errorMessage: LiveData<String> = historyRepository.errorMessage
    val isLoading: LiveData<Boolean> = historyRepository.isLoading


    fun addHistory(
        foodPhoto: File,
        foodName: String,
        lectineStatus: Boolean,
        ingredients: List<IngredientsItem>,
    ) = historyRepository.addHistory(foodPhoto, foodName, lectineStatus, ingredients)

    fun getHistoriesLimited() = historyRepository.getHistoriesLimited()

    fun getDetailHistory(historyId: String) = historyRepository.getDetailHistory(historyId)


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