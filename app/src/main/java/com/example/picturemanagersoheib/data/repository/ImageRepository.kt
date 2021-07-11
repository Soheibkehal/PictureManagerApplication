package com.example.picturemanagersoheib.data.repository

import com.example.picturemanagersoheib.data.models.DetailedImage
import com.example.picturemanagersoheib.data.models.Image
import com.example.picturemanagersoheib.data.models.ImageFeed
import com.example.picturemanagersoheib.data.models.ImagePermissionBody
import com.example.picturemanagersoheib.utils.RetrofitClient
import com.example.picturemanagersoheib.utils.SessionManager
import okhttp3.RequestBody
import java.io.File
import javax.inject.Singleton

class ImageRepository {

    suspend fun getImageByUserId(userId: Int): List<Image> {
        val response = RetrofitClient.apiService.getImageByUser(token = "Bearer " + SessionManager().fetchAuthToken()!!,id = userId)
        return response.body()!!
    }

    suspend fun getImageByAlbumId(albumId : Int): List<Image> {
        val response = RetrofitClient.apiService.getAlbumById(token = "Bearer " + SessionManager().fetchAuthToken()!!,id = albumId)
        return response.body()!!.images
    }

    suspend fun getImageById(imageId : Int): DetailedImage {
        val response = RetrofitClient.apiService.getImageById(token = "Bearer " + SessionManager().fetchAuthToken()!!,id = imageId)
        return response.body()!!
    }

    suspend fun getUserFeed(): List<ImageFeed> {
        val response = RetrofitClient.apiService.getUserFeed(token = "Bearer " + SessionManager().fetchAuthToken()!!)
        return response.body()!!
    }
    suspend fun addPermission(imageId: Int, userId: Int) {
            RetrofitClient.apiService.addImagePermission(token = "Bearer " + SessionManager().fetchAuthToken()!!, ImagePermissionBody(imageId, userId))
    }

    suspend fun removePermission(imageId: Int, userId: Int) {
        RetrofitClient.apiService.removeImagePermission(token = "Bearer " + SessionManager().fetchAuthToken()!!, ImagePermissionBody(imageId, userId))
    }

    suspend fun uploadImage(requestBody: RequestBody ) {
        RetrofitClient.apiService.uploadImage(token = "Bearer " + SessionManager().fetchAuthToken()!!, requestBody)
    }

}