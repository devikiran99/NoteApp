package com.devikiran.note_app.utils


object  Utils {
    fun isNotValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return !(email.trim().matches(emailRegex))
    }

    fun isNotValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$".toRegex()
        return !(password.matches(passwordRegex))
    }
}
