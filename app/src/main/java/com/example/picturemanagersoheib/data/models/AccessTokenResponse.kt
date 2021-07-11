package com.example.picturemanagersoheib.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccessTokenResponse(
    @PrimaryKey
    val accessToken :String,
    val userId: Int
)