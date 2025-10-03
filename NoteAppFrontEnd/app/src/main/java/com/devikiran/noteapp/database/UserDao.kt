package com.devikiran.noteapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.devikiran.noteapp.data.db.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun getUserList(): List<User>

    @Upsert
    suspend fun upsertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}