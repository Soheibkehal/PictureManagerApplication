package com.example.picturemanagersoheib.data.models

import java.sql.Timestamp

data class ImageFeed(val id: Int, val name: String, val timestamp: String, val metadata: String, val user: User) {

}