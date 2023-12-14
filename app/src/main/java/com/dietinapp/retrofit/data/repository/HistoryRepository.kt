package com.dietinapp.retrofit.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dietinapp.retrofit.api.ApiService
import com.dietinapp.retrofit.response.DeleteResponse
import com.dietinapp.retrofit.response.HistoryResponse
import com.dietinapp.retrofit.response.IngredientsItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HistoryRepository private constructor(
    private val apiService: ApiService
){
    private val _historyResponse = MutableLiveData<HistoryResponse>()
    private val historyResponse: LiveData<HistoryResponse> = _historyResponse

    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun addHistory(
        foodPhoto: File,
        foodName: String,
        lectineStatus: Boolean,
        ingredients: List<IngredientsItem>,
    ){
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

            val requestFoodName = foodName.toRequestBody("text/plain".toMediaTypeOrNull())

            val gson = Gson()
            val json = gson.toJson(ingredients)
            val requestIngredients = json.toRequestBody("application/json".toMediaType())

            val imageFile = foodPhoto.asRequestBody("image/jpeg".toMediaType())
            val requestFoodPhoto = MultipartBody.Part.createFormData(
                "image",
                foodPhoto.name,
                imageFile)

            val client =  apiService.addHistory(requestFoodPhoto, requestFoodName, lectineStatus, requestIngredients)

            client.enqueue(object: Callback<HistoryResponse> {
                override fun onResponse(
                    call: Call<HistoryResponse>,
                    response: Response<HistoryResponse>
                ) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        _successMessage.value = response.message().toString()
                    } else {
                        _isLoading.value = false
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

    }

    fun deleteHistory(
        historyId: String
    ){
        _errorMessage.value = ""
        _successMessage.value = ""

        val client = apiService.deleteHistory(historyId)

        client.enqueue(object: Callback<DeleteResponse> {
            override fun onResponse(
                call: Call<DeleteResponse>,
                response: Response<DeleteResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _successMessage.value = response.message().toString()
                } else {
                    _errorMessage.value = response.message().toString()
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                _isLoading.value = false
                val errorResponse = t.message
                _errorMessage.value = errorResponse.toString()
                Log.e("Detail History Repo", "onFailure: $errorResponse")
            }

        })
    }

    fun getDetailHistory(
        historyId: String
    ): LiveData<HistoryResponse>{
        _isLoading.value = true
        _errorMessage.value = ""
        _successMessage.value = ""

        val client = apiService.getDetailHistory(historyId)

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
                Log.e("Detail History Repo", "onFailure: $errorResponse")
            }

        })

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