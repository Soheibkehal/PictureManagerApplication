package com.example.picturemanagersoheib.data.models

data class DetailedImage(val id: Int, val name: String?, val timestamp: String?, val metadata: String?, val user: User, val imagePermissions: List<UserPermission>)