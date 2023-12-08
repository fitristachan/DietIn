package com.dietinapp.retrofit.api

import com.dietinapp.retrofit.response.HistoriesResponse
import com.dietinapp.retrofit.response.AddHistoryResponse
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
    suspend fun getHistoryLimited(

    ): HistoriesResponse

    @GET("history/{historyId}")
    suspend fun getDetailHistory(
        @Path("historyId") historyId: String

    ): HistoriesResponse

    @Multipart
    @POST("history")
    fun addHistory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddHistoryResponse>
}