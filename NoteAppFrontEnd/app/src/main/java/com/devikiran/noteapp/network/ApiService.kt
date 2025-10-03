package com.devikiran.noteapp.network

import com.devikiran.noteapp.data.api.AuthenticationRequest
import com.devikiran.noteapp.data.api.NoteRequestData
import com.devikiran.noteapp.data.api.TokenValidationRequest
import com.devikiran.noteapp.data.db.Note
import com.devikiran.noteapp.data.db.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    /** User **/
    @POST("/auth/register")
    suspend fun register(@Body authenticationRequest: AuthenticationRequest): Response<Void>

    @POST("/auth/login")
    suspend fun login(@Body authenticationRequest: AuthenticationRequest): Response<User>

    @POST("/auth/updateProfile")
    fun updateProfile(@Body authenticationRequest: AuthenticationRequest): Response<Void>

    @POST("/auth/logout")
    suspend fun logout(@Body authenticationRequest: AuthenticationRequest): Response<Void>

    @POST("/auth/refresh")
    suspend fun refreshToken(
        @Body tokenValidationRequest: TokenValidationRequest
    ): Response<User>

    @POST("/auth/verifyAccessToken")
    suspend fun isAccessTokenValid(
        @Body token: TokenValidationRequest
    ): Response<Boolean>


    @POST("/auth/verifyRefreshToken")
    suspend fun isRefreshTokenValid(
        @Body token: TokenValidationRequest
    ): Response<Boolean>

    @POST("/auth/resetPassword")
    suspend fun resetPassword(@Body authenticationRequest: AuthenticationRequest): Response<User>


    /** Note  **/
    @POST("/notes")
    suspend fun upsertNote(
        @Header("Authorization") token: String,
        @Body note: NoteRequestData
    ): Response<Note>

    @DELETE("/notes/{id}")
    suspend fun deleteNote(
        @Header("Authorization") token: String,
        @Path("id") noteId: String
    ): Response<Void>

    @GET("/notes")
    suspend fun getNotes(
        @Header("Authorization") token: String): Response<List<Note>>


}