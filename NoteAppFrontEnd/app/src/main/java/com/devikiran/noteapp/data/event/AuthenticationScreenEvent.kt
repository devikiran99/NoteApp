package com.devikiran.noteapp.data.event

sealed class AuthenticationScreenEvent {
    data class OnUserName(val userName: String): AuthenticationScreenEvent()
    data class OnEmail(val email: String): AuthenticationScreenEvent()

    data class OnUploadImage(val imageUri: String): AuthenticationScreenEvent()

    data class OnAuthScreen(val selectedPage: Int): AuthenticationScreenEvent()
    data class OnEnterPassword(val password: String): AuthenticationScreenEvent()
    data class OnReEnterPassword(val repeatPassword: String): AuthenticationScreenEvent()
    data object OnPasswordVisible : AuthenticationScreenEvent()
    data object OnAuthenticate : AuthenticationScreenEvent()

    data object OnShowForgotPassword : AuthenticationScreenEvent()

    data object OnResetPassword : AuthenticationScreenEvent()

    data object OnClearData : AuthenticationScreenEvent()

    data object OnClearErrorMsg : AuthenticationScreenEvent()
}