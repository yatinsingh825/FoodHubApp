package com.example.foodhub_android.data

import retrofit2.http.GET

interface FoodApi {
    @GET("food")
    suspend fun getFood(): List<String>
}