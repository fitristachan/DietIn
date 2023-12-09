package com.dietinapp.retrofit.data.repository

import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dietinapp.model.Ingredient
import com.dietinapp.retrofit.api.ApiService
import com.dietinapp.retrofit.response.HistoriesResponse
import com.dietinapp.retrofit.response.HistoryResponse
import com.dietinapp.utils.readRecipesFromJson
import com.dietinapp.utils.reduceFileImage
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HistoryRepository private constructor(
    private val apiService: ApiService
){
    private val _historiesResponse = MutableLiveData<HistoriesResponse>()
    private val historiesResponse: LiveData<HistoriesResponse> = _historiesResponse

    private val _historyResponse = MutableLiveData<HistoryResponse>()
    private val historyResponse: LiveData<HistoryResponse> = _historyResponse

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun addHistory(
        foodPhoto: File,
        foodName: String,
        lectineStatus: Boolean,
        ingredients: List<Ingredient>,
    ): LiveData<HistoryResponse>{
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        val requestFoodName = foodName.toRequestBody("text/plain".toMediaType())
        val gson = Gson()
        val json = gson.toJson(ingredients)
        val requestIngredients = json.toRequestBody("application/json".toMediaType())
        val requestImageFile = foodPhoto.asRequestBody("image/jpeg".toMediaType())
        val requestFoodPhoto = MultipartBody.Part.createFormData(
            "foodPhoto",
            foodPhoto.name,
            requestImageFile)

        val client = apiService.addHistory(requestFoodPhoto, requestFoodName, lectineStatus, requestIngredients)

        client.enqueue(object: Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _historyResponse.value = response.body()
                    _successMessage.value = response.message().toString()
                } else {
                    _errorMessage.value = response.message().toString()
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                _isLoading.value = false
                val errorResponse = t.message
                _errorMessage.value = errorResponse.toString()
                Log.e("Add History Repo", "onFailure: $errorResponse")
            }

        })

        return historyResponse
    }

    fun getHistoriesLimited(): LiveData<HistoriesResponse>{
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        val client = apiService.getHistoriesLimited()

        return historiesResponse
    }

    fun getDetailHistory(
        historyId: String
    ): LiveData<HistoryResponse>{
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        val client = apiService.getDetailHistory(historyId)

        return historyResponse
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(
            apiService: ApiService
        ): HistoryRepository = instance ?: synchronized(this) {
            instance ?: HistoryRepository(apiService)
        }.also { instance = it }
    }
}