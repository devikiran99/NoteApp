package com.devikiran.noteapp.screens.holder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devikiran.noteapp.data.api.TokenValidationRequest
import com.devikiran.noteapp.data.event.NoteProgressEvent
import com.devikiran.noteapp.data.navigation.AuthenticationData
import com.devikiran.noteapp.data.ui.NoteListScreenData
import com.devikiran.noteapp.screens.utils.NoteRepository
import com.devikiran.noteapp.screens.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolderViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {

    private var _navigateTo = MutableStateFlow<Any?>(null)
    val navigateTo: StateFlow<Any?> = _navigateTo


    init {
        isTokenValid()
    }

    fun isTokenValid() {
        viewModelScope.launch {
            val user = repository.getUserList().firstOrNull()

            if (user != null) {
                val accessToken = user.accessToken
                val refreshToken = user.refreshToken

                if (accessToken.isNotBlank() || refreshToken.isNotBlank()) {

                    val isRefreshTokenValid = repository.isRefreshTokenValid(
                        TokenValidationRequest(
                            refreshToken
                        )
                    )
                    if (isRefreshTokenValid) {
                        _navigateTo.update {
                            NoteListScreenData
                        }
                    } else {
                        val isAccessTokenValid =
                            repository.isAccessTokenValid(TokenValidationRequest(accessToken))
                        if (isAccessTokenValid) {
                            repository.refreshToken { tokenState ->
                                when (tokenState) {
                                    NoteProgressEvent.Completed -> {}

                                    is NoteProgressEvent.Fail -> {
                                        _navigateTo.update { AuthenticationData }
                                    }

                                    NoteProgressEvent.Start, NoteProgressEvent.Progress -> {
                                        _navigateTo.update { "LoadingScreen" }
                                    }

                                    is NoteProgressEvent.Success<String> -> {
                                        _navigateTo.update { NoteListScreenData }
                                    }
                                }
                            }
                        } else {
                            _navigateTo.update { NoteListScreenData }
                        }
                    }
                } else {
                    _navigateTo.update { AuthenticationData }
                }
            } else {
                _navigateTo.update { AuthenticationData }
            }
        }
    }
}