package com.example.picturemanagersoheib.data.repository

import android.app.Application
import androidx.room.Room
import com.example.PictureManagerApplication
import com.example.picturemanagersoheib.data.dao.UserDao
import com.example.picturemanagersoheib.data.database.UserDatabase
import com.example.picturemanagersoheib.data.models.ImagePermissionBody
import com.example.picturemanagersoheib.data.models.User
import com.example.picturemanagersoheib.ui.activities.MainActivity
import com.example.picturemanagersoheib.utils.RetrofitClient
import com.example.picturemanagersoheib.utils.SessionManager
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository(private val userDao: UserDao) {

    suspend fun getUsers() : List<User> {
        val loadedUsers : List<User> = userDao.list()
        if(loadedUsers.isNotEmpty()) {
            return loadedUsers
        }
        val response : Response<List<User>> = RetrofitClient.apiService.getUsers()
        userDao.save(response.body()!!)
        return response.body()!!
    }

    suspend fun addPermission(imageId: Int, userId: Int) {
        RetrofitClient.apiService.addImagePermission(token = "Bearer " + SessionManager().fetchAuthToken()!!, ImagePermissionBody(imageId, userId))
    }

    suspend fun removePermission(imageId: Int, userId: Int) {
        RetrofitClient.apiService.removeImagePermission(token = "Bearer " + SessionManager().fetchAuthToken()!!, ImagePermissionBody(imageId, userId))
    }
}