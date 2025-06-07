package com.example.quicknews.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/google")
    suspend fun googleLogin(@Body idTokenBody: IdTokenRequest): AuthResponse
}

data class IdTokenRequest(val idToken: String)

data class AuthResponse(
    val token: String,
    val user: UserPayload
)

data class UserPayload(
    val userId: String,
    val email: String,
    val name: String,
    val picture: String?
)
