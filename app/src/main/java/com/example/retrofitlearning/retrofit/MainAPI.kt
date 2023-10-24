package com.example.retrofitlearning.retrofit

import retrofit2.http.Body
import retrofit2.http.POST

interface MainAPI {
    @POST("auth/login")
    suspend fun auth(@Body outputData: OutputData): User
}