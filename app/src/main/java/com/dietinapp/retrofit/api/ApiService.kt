package com.dietinapp.retrofit.api

import com.dietinapp.retrofit.response.HistoriesResponse
import com.dietinapp.retrofit.response.HistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Part
import retrofit2.http.Multipart
import retrofit2.http.Query

interface ApiService {
    @GET("history")
    suspend fun getHistories(
        @Query("page") page: Int = 1, @Query("size") size: Int = 20
    ): HistoriesResponse

    @GET("history")
    fun getHistoriesLimited(): Call<HistoriesResponse>

    @GET("history/{historyId}")
    fun getDetailHistory(
        @Path("historyId") historyId: String
    ): Call<HistoryResponse>

    @Multipart
    @POST("history")
    fun addHistory(
        @Part foodPhoto: MultipartBody.Part,
        @Part("foodName") foodName: RequestBody,
        @Part("lectineStatus") lectineStatus: Boolean,
        @Part("ingredients") ingredients: RequestBody,
    ): Call<HistoryResponse>
}