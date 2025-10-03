package com.devikiran.noteapp.screens.utils

import com.devikiran.noteapp.data.api.AuthenticationRequest
import com.devikiran.noteapp.data.api.NoteRequestData
import com.devikiran.noteapp.data.api.TokenValidationRequest
import com.devikiran.noteapp.data.db.Note
import com.devikiran.noteapp.data.db.User
import com.devikiran.noteapp.data.event.NoteProgressEvent
import com.devikiran.noteapp.database.AppDataBase
import com.devikiran.noteapp.network.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val apiService: ApiService,
    private val appDataBase: AppDataBase,
    private val preferenceHelper: PreferenceHelper
) {

    private val userDao = appDataBase.UserDao()
    private val noteDao = appDataBase.NoteDao()

    suspend fun getUserList() = userDao.getUserList()

    fun getNoteList() = noteDao.getNoteList()

    suspend fun upsertUser(user: User) = userDao.upsertUser(user)

    suspend fun upsertNoteList(noteList: List<Note>) = noteDao.upsertNoteList(noteList)

    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)


    suspend fun registerUser(
        authenticationRequest: AuthenticationRequest,
        onProcessingEvent: (NoteProgressEvent<String>) -> Unit
    ) = withContext(IO) {
        try {
            onProcessingEvent(NoteProgressEvent.Start)
            val response = apiService.register(authenticationRequest)
            if (response.isSuccessful) {
                onProcessingEvent(NoteProgressEvent.Success("Registration Successfully"))
            } else {
                onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
            }
        } catch (e: Exception) {
            onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
        } finally {
            delay(1000)
            onProcessingEvent(NoteProgressEvent.Completed)
        }
    }

    suspend fun loginUser(
        authenticationRequest: AuthenticationRequest,
        onProcessingEvent: (NoteProgressEvent<User>) -> Unit
    ) = withContext(IO) {
        try {
            onProcessingEvent(NoteProgressEvent.Start)
            val response = apiService.login(authenticationRequest)
            if (response.isSuccessful) {
                val user = response.body()!!
                upsertUser(user)
                onProcessingEvent(NoteProgressEvent.Success(response.body()!!))
            } else {
                onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
            }
        } catch (e: Exception) {
            onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
        } finally {
            delay(1000)
            onProcessingEvent(NoteProgressEvent.Completed)
        }
    }

    suspend fun updateUserProfile(
        user: User,
        authRequest: AuthenticationRequest,
        onProcessingEvent: (NoteProgressEvent<String>) -> Unit
    ) = withContext(IO) {
        try {
            onProcessingEvent(NoteProgressEvent.Start)
            val response = apiService.updateProfile(authRequest)
            if (response.isSuccessful) {
                val updatedUser = user.copy(userName = authRequest.userName, imageUri = authRequest.imageUri)
                upsertUser(updatedUser)
                onProcessingEvent(NoteProgressEvent.Success("Profile updated successfully"))
            } else {
                onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
            }
        } catch (e: Exception) {
            onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
        } finally {
            delay(1000)
            onProcessingEvent(NoteProgressEvent.Completed)
        }
    }

    suspend fun resetPassword(
        authenticationRequest: AuthenticationRequest,
        onProcessingEvent: (NoteProgressEvent<String>) -> Unit
    ) = withContext(IO) {
        try {
            onProcessingEvent(NoteProgressEvent.Start)
            val response = apiService.resetPassword(authenticationRequest)
            if (response.isSuccessful) {
                upsertUser(response.body()!!)
                onProcessingEvent(NoteProgressEvent.Success("Password Reset Successfully"))
            } else {
                onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
            }
        } catch (e: Exception) {
            onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
        } finally {
            delay(1000)
            onProcessingEvent(NoteProgressEvent.Completed)
        }
    }

    suspend fun logoutUser(
        user: User,
        authenticationRequest: AuthenticationRequest,
        onProcessingEvent: (NoteProgressEvent<String>) -> Unit
    ) = withContext(IO) {
        try {
            onProcessingEvent(NoteProgressEvent.Start)
            val response = apiService.logout(authenticationRequest)
            if (response.isSuccessful) {
                deleteUser(user)
                onProcessingEvent(NoteProgressEvent.Success("Logged Out"))
            } else {
                onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
            }
        } catch (e: Exception) {
            onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
        } finally {
            delay(1000)
            onProcessingEvent(NoteProgressEvent.Completed)
        }
    }

    suspend fun addNote(
        noteRequestData: NoteRequestData,
        accessToken: String,
        onProcessingEvent: (NoteProgressEvent<String>) -> Unit
    ) = withContext(IO) {
        try {
            onProcessingEvent(NoteProgressEvent.Start)
            val token = "Bearer $accessToken"
            val response = apiService.upsertNote(token, noteRequestData)
            if (response.isSuccessful) {
                async { upsertNoteList(listOf(response.body()!!)) }.await()
                onProcessingEvent(NoteProgressEvent.Success("Successfully Added"))
            } else {
                onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
            }
        } catch (e: Exception) {
            onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
        } finally {
            delay(1000)
            onProcessingEvent(NoteProgressEvent.Completed)
        }
    }

    suspend fun deleteNote(
        note: Note,
        accessToken: String,
        onProcessingEvent: (NoteProgressEvent<String>) -> Unit
    ) =
        withContext(IO) {
            try {
                onProcessingEvent(NoteProgressEvent.Start)
                val token = "Bearer $accessToken"
                val response = apiService.deleteNote(token, note.id)
                if (response.isSuccessful) {
                    deleteNote(note)
                    onProcessingEvent(NoteProgressEvent.Success("Deleted Successfully"))
                } else {
                    onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
                }
            } catch (e: Exception) {
                onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
            } finally {
                delay(1000)
                onProcessingEvent(NoteProgressEvent.Completed)
            }
        }

    suspend fun getNotes(accessToken: String,onProcessingEvent: (NoteProgressEvent<String>) -> Unit) =
        withContext(IO) {
            try {
                onProcessingEvent(NoteProgressEvent.Start)
                val token = "Bearer $accessToken"
                val response = apiService.getNotes(token)
                if (response.isSuccessful) {
                    async { upsertNoteList(response.body()!!) }.await()
                    onProcessingEvent(NoteProgressEvent.Success("Fetched Successfully"))
                } else {
                    onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
                }
            } catch (e: Exception) {
                onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
            } finally {
                delay(1000)
                onProcessingEvent(NoteProgressEvent.Completed)
            }
        }

    suspend fun refreshToken(onProcessingEvent: (NoteProgressEvent<String>) -> Unit) =
        withContext(IO) {
            try {
                val token = TokenValidationRequest(
                    token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2ODc1MTUwNTQ3ODVjYjY3NzJhNTczMDgiLCJ0eXBlIjoicmVmcmVzaCIsImlhdCI6MTc1MjUwNjQ0NywiZXhwIjoxNzU1MDk4NDQ3fQ.6Iz9zliAt-x5jL_hAqKFcC3mwIkLHDRdrag6XYpJ9us"
                )
                val response = apiService.refreshToken(token)
                if (response.isSuccessful) {
                    upsertUser(response.body()!!)
                    onProcessingEvent(NoteProgressEvent.Success("Success"))
                } else {
                    onProcessingEvent(NoteProgressEvent.Fail(getErrorResponse(response)))
                }
            } catch (e: Exception) {
                onProcessingEvent(NoteProgressEvent.Fail(e.message.toString()))
            } finally {
                delay(100)
                onProcessingEvent(NoteProgressEvent.Completed)
            }
        }

    suspend fun isAccessTokenValid(
        token: TokenValidationRequest
    ): Boolean = withContext(IO) {
        return@withContext try {
            val response = apiService.isAccessTokenValid(token)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isRefreshTokenValid(
        token: TokenValidationRequest
    ): Boolean = withContext(IO) {
        return@withContext try {
            val response = apiService.isRefreshTokenValid(token)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun getErrorResponse(response: Response<*>): String {
        data class ErrorResponse(
            val status: Int = -1,
            val message: String = "",
            val errors: List<String>?
        )

        return try {
            val gson = Gson()
            val errorJson = response.errorBody()?.string()
            val errorResponse = gson.fromJson(errorJson, ErrorResponse::class.java)
            errorResponse.errors?.joinToString(" ") ?: errorResponse.message
        } catch (e: Exception) {
            e.message ?: "Error parsing server response"
        }
    }
}