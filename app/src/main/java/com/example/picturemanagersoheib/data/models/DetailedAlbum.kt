package com.example.picturemanagersoheib.data.models

import java.security.Permissions


data class DetailedAlbum (val id: Int, val name: String, val timestamp: String, val images : List<Image>, val albumPermissions : List<UserPermission>)