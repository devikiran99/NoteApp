package com.devikiran.noteapp.data.ui

data class AuthenticationScreenData(
    val userName: String = "",
    val email: String = "",
    val password: String = "",
    val rePassword: String = "",
    val selectedPage: Int = 0,
    val errorMessage: String = "",
    val isRegister: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isPasswordMatches: Boolean = false,
    val enableForgotPassword: Boolean = false
)