package com.devikiran.note_app.controller

import com.devikiran.note_app.security.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    data class AuthRequest(
        val userName: String,
        val imageUri: String,
        val email: String,
        val password: String
    )

    data class TokenValidationRequest(
        val token: String
    )

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: AuthRequest
    ) {
        authService.register(body)
    }

    @PostMapping("/updateProfile")
    fun updateProfile(
        @Valid @RequestBody body: AuthRequest
    ) {
        authService.updateProfile(body)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: AuthRequest
    ): AuthService.RegistrationResponse {
        return authService.login(body)
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody body: AuthRequest
    ) {
        authService.logout(body)
    }

    @PostMapping("/resetPassword")
    fun resetPassword(
        @RequestBody body: AuthRequest
    ) {
        authService.resetPassword(authRequest = body)
    }

    @PostMapping("/delete")
    fun deleteUser(
        @RequestBody body: AuthRequest
    ) {
        return authService.deleteUser(body)
    }

    @PostMapping("/update")
    fun updateUser(
        @RequestBody body: AuthRequest
    ): AuthService.RegistrationResponse {
        return authService.updateUser(body)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: TokenValidationRequest
    ): AuthService.RegistrationResponse {
        return authService.refresh(body.token)
    }

    @PostMapping("/verifyAccessToken")
    fun isAccessTokenValid(
        @RequestBody body: TokenValidationRequest
    ): Boolean {
        return authService.isAccessTokenValid(token = body.token)
    }

    @PostMapping("/verifyRefreshToken")
    fun isRefreshTokenValid(
        @RequestBody body: TokenValidationRequest
    ): Boolean {
        return authService.isRefreshTokenValid(token= body.token)
    }
}