package com.example.picturemanagersoheib.data.repository

import com.example.picturemanagersoheib.data.models.*
import com.example.picturemanagersoheib.utils.RetrofitClient
import com.example.picturemanagersoheib.utils.SessionManager

class AlbumRepository {

    suspend fun getAlbumByUserId(userId: Int): List<Album> {
        val response = RetrofitClient.apiService.getAlbumByUser(token = "Bearer " + SessionManager().fetchAuthToken()!!,id = userId)
        return response.body()!!
    }

    suspend fun getAlbumById(albumId : Int): DetailedAlbum {
        val response = RetrofitClient.apiService.getAlbumById(token = "Bearer " + SessionManager().fetchAuthToken()!!,id = albumId)
        return response.body()!!
    }

    suspend fun addPermission(albumId: Int, userId: Int) {
        RetrofitClient.apiService.addAlbumPermission(token = "Bearer " + SessionManager().fetchAuthToken()!!, AlbumPermissionBody(albumId, userId))
    }

    suspend fun removePermission(albumId: Int, userId: Int) {
        RetrofitClient.apiService.removeAlbumPermission(token = "Bearer " + SessionManager().fetchAuthToken()!!, AlbumPermissionBody(albumId, userId))
    }
}