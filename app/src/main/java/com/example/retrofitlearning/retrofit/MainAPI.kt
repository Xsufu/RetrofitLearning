package com.example.retrofitlearning.retrofit

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MainAPI {
    @POST("auth/login")
    suspend fun auth(@Body outputData: OutputData): User

    @GET("product")
    suspend fun getAllProducts(): Products

    @GET("products/search")
    suspend fun getProductBySearch(@Query ("q") searchQuery: String): Products
}