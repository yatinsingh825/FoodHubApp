package com.example.foodhub_android.data

import androidx.activity.result.IntentSenderRequest
import com.example.foodhub_android.data.models.AuthResponse
import com.example.foodhub_android.data.models.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("food")
    suspend fun getFood(): List<String>
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse
}