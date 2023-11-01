package com.example.retrofitlearning.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface MainAPI {
    @POST("auth/login")
    suspend fun auth(@Body outputData: OutputData): Response<User>

    @Headers("Content-Type: application/json")
    @GET("auth/product")
    suspend fun getAllProducts(
        @Header("Authorization") token: String
    ): Products

    @Headers("Content-Type: application/json")
    @GET("auth/products/search")
    suspend fun getProductBySearch(
        @Header("Authorization") token: String,
        @Query("q") searchQuery: String
    ): Products
}