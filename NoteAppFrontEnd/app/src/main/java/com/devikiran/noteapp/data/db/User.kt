package com.devikiran.noteapp.data.db

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Keep
@Entity(tableName = "user")
@Serializable
data class User(
    @PrimaryKey(autoGenerate = false)
    val email: String,
    val userName: String,
    val imageUri: String,
    val accessToken: String,
    val refreshToken: String
)