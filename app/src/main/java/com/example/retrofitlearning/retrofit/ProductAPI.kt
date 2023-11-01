package com.example.retrofitlearning.retrofit

import retrofit2.http.GET
import retrofit2.http.Path

interface ProductAPI {
    @GET("auth/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product
}