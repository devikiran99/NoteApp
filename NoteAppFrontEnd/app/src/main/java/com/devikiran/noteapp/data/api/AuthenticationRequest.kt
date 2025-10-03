package com.devikiran.noteapp.data.api

data class AuthenticationRequest(
    val userName: String,
    val imageUri: String,
    val email: String,
    val password: String
)