package com.dietinapp.retrofit.api

import com.dietinapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getApiService(token: String): ApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders =
                req.newBuilder().addHeader("historyAuth", "$token").build()
            chain.proceed(requestHeaders)
        }

        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor).build()

        val apiUrl = BuildConfig.BASE_URL
        val retrofit =
            Retrofit.Builder().baseUrl(apiUrl).addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
        return retrofit.create(ApiService::class.java)
    }
}