package com.example.picturemanagersoheib.data.repository

import com.example.picturemanagersoheib.data.models.AccessTokenResponse
import com.example.picturemanagersoheib.data.models.DefaultResponses
import com.example.picturemanagersoheib.data.models.LoginCredentials
import com.example.picturemanagersoheib.data.models.RegisterCredentials
import com.example.picturemanagersoheib.utils.RetrofitClient
import javax.inject.Inject
import javax.inject.Singleton



class AuthRepository {

    suspend fun login(info: LoginCredentials): AccessTokenResponse {
        val response = RetrofitClient.apiService.login(info)
        return response.body()!!
    }

    suspend fun register(info: RegisterCredentials): DefaultResponses {
        val response = RetrofitClient.apiService.register(info)
        return response.body()!!
    }
}