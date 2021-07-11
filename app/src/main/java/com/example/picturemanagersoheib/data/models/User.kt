package com.example.picturemanagersoheib.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: Int,
    val login: String,
    val mail: String
)