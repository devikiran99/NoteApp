package com.devikiran.note_app.security

import com.devikiran.note_app.controller.AuthController.AuthRequest
import com.devikiran.note_app.database.model.RefreshToken
import com.devikiran.note_app.database.model.User
import com.devikiran.note_app.database.repository.RefreshTokenRepository
import com.devikiran.note_app.database.repository.UserRepository
import com.devikiran.note_app.utils.Utils
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    data class RegistrationResponse(
        val userName: String,
        val email: String,
        val imageUri: String,
        val accessToken: String,
        val refreshToken: String
    )

    fun register(authRequest: AuthRequest): User {

        if(Utils.isNotValidEmail(authRequest.email)) {
            throw BadCredentialsException("Invalid email format.")
        }

        if(Utils.isNotValidPassword(authRequest.password)) {
            throw BadCredentialsException("Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character.")
        }

        val user = userRepository.findByEmail(authRequest.email.trim())
        if(user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists.")
        }

        return userRepository.save(
            User(
                email = authRequest.email,
                hashedPassword = hashEncoder.encode(authRequest.password),
                userName = authRequest.userName,
                imageUri = authRequest.imageUri
            )
        )
    }

    fun updateProfile(authRequest: AuthRequest): User {

        val user = userRepository.findByEmail(authRequest.email)
            ?: throw BadCredentialsException("Problems while updating try again.")

        return userRepository.save(
            User(
                id = user.id,
                email = user.email,
                hashedPassword = user.hashedPassword,
                userName = authRequest.userName,
                imageUri = authRequest.imageUri,
            )
        )
    }

    fun login(authRequest: AuthRequest): RegistrationResponse {

        if(Utils.isNotValidEmail(authRequest.email)) {
            throw BadCredentialsException("Invalid email format.")
        }

        if(Utils.isNotValidPassword(authRequest.password)) {
            throw BadCredentialsException("Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character.")
        }

        val user = userRepository.findByEmail(authRequest.email)
            ?: throw BadCredentialsException("Invalid credentials.")

        if(!hashEncoder.matches(authRequest.password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials.")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return RegistrationResponse(
            userName = user.userName,
            email = user.email,
            imageUri = user.imageUri,
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    fun logout(authRequest: AuthRequest) {

        val user = userRepository.findByEmail(authRequest.email)
            ?: throw BadCredentialsException("Invalid credentials.")

        val expiresAt = Instant.now()

        refreshTokenRepository.save(
            RefreshToken(
                userId = user.id,
                expiresAt = expiresAt,
                hashedToken = ""
            )
        )
    }

    fun resetPassword(authRequest: AuthRequest) {

        if(Utils.isNotValidEmail(authRequest.email)) {
            throw BadCredentialsException("Invalid email format.")
        }

        if(Utils.isNotValidPassword(authRequest.password)) {
            throw BadCredentialsException("Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character.")
        }

        val user = userRepository.findByEmail(authRequest.email)
            ?: throw BadCredentialsException("Email id not registered yet")

        userRepository.save(
            User(
                email = user.email,
                hashedPassword = hashEncoder.encode(authRequest.password),
                userName = user.userName,
                imageUri = user.imageUri
            )
        )
    }
    fun deleteUser(authRequest: AuthRequest) {
        val user = userRepository.findByEmail(authRequest.email)
            ?: throw BadCredentialsException("Invalid credentials.")

        userRepository.delete(user)
    }

    fun updateUser(authRequest: AuthRequest): RegistrationResponse {
        val user = userRepository.findByEmail(authRequest.email)
            ?: throw BadCredentialsException("Invalid credentials.")

        val password = if(authRequest.password.isNotBlank()) {
            if (Utils.isNotValidPassword(authRequest.password)) {
                throw BadCredentialsException("Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character.")
            }
            hashEncoder.encode(authRequest.password)
        } else {
            user.hashedPassword
        }

        userRepository.save(
            User(
                id = user.id,
                email = authRequest.email,
                hashedPassword = password,
                userName = authRequest.userName,
                imageUri = authRequest.imageUri
            )
        )

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return RegistrationResponse(
            userName = authRequest.userName,
            email = authRequest.email,
            imageUri = authRequest.imageUri,
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    fun isAccessTokenValid(token: String): Boolean {
        return jwtService.validateAccessToken(token)
    }

    fun isRefreshTokenValid(token: String): Boolean {
        return jwtService.validateRefreshToken(token)
    }

    @Transactional
    fun refresh(refreshToken: String): RegistrationResponse {
        if(!jwtService.validateRefreshToken(refreshToken)) {
            throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid refresh token.")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid refresh token.")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw ResponseStatusException(
                HttpStatusCode.valueOf(401),
                "Refresh token not recognized (maybe used or expired?)"
            )

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)

        return RegistrationResponse(
            userName = user.userName,
            email = user.email,
            imageUri = user.imageUri,
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}