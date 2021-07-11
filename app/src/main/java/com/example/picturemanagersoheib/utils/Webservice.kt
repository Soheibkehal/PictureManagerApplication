package com.example.picturemanagersoheib.utils

import com.example.picturemanagersoheib.data.models.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

interface Webservice {

    //USER
    @Headers("Content-Type:application/json")
    @POST("user/login")
    suspend fun login(@Body info: LoginCredentials): Response<AccessTokenResponse>

    @Headers("Content-Type:application/json")
    @POST("user")
    suspend fun register(@Body info: RegisterCredentials): Response<DefaultResponses>

    @Headers("Content-Type:application/json")
    @GET("user")
    suspend fun getUsers(): Response<List<User>>

    //IMAGES
    @Headers("Content-Type:application/json")
    @GET("image/user/{id}")
    suspend fun getImageByUser(@Header("Authorization") token: String, @Path("id") id : Int): Response<List<Image>>

    @Headers("Content-Type:application/json")
    @GET("image/{id}")
    suspend fun getImageById(@Header("Authorization") token: String, @Path("id") id : Int): Response<DetailedImage>

    @Headers("Content-Type:application/json")
    @POST("image/permission/add")
    suspend fun addImagePermission(@Header("Authorization") token: String, @Body body: ImagePermissionBody): Response<DefaultResponses>

    @Headers("Content-Type:application/json")
    @POST("image/permission/delete")
    suspend fun removeImagePermission(@Header("Authorization") token: String, @Body body: ImagePermissionBody): Response<DefaultResponses>

    @Headers("Content-Type:application/json")
    @GET("user/feed")
    suspend fun getUserFeed(@Header("Authorization") token: String): Response<List<ImageFeed>>

    @Multipart
    @POST("image")
    suspend fun uploadImage(@Header("Authorization") token: String, @Part("image\"; filename=\"upload.png\" ") image : RequestBody): Response<DefaultResponses>

    //ALBUMS
    @Headers("Content-Type:application/json")
    @GET("album/user/{id}")
    suspend fun getAlbumByUser(@Header("Authorization") token: String, @Path("id") id : Int): Response<List<Album>>

    @Headers("Content-Type:application/json")
    @GET("album/{id}")
    suspend fun getAlbumById(@Header("Authorization") token: String, @Path("id") id : Int): Response<DetailedAlbum>

    @Headers("Content-Type:application/json")
    @POST("album/permission/add")
    suspend fun addAlbumPermission(@Header("Authorization") token: String, @Body body: AlbumPermissionBody): Response<DefaultResponses>

    @Headers("Content-Type:application/json")
    @POST("album/permission/delete")
    suspend fun removeAlbumPermission(@Header("Authorization") token: String, @Body body: AlbumPermissionBody): Response<DefaultResponses>
}