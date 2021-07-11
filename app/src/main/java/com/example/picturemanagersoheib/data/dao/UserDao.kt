package com.example.picturemanagersoheib.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.picturemanagersoheib.data.models.User

@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    fun save(users: List<User>)

    @Query("SELECT * FROM user")
    fun list(): List<User>
}