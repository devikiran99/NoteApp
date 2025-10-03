package com.devikiran.noteapp.screens.authenticate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devikiran.noteapp.data.api.AuthenticationRequest
import com.devikiran.noteapp.data.db.User
import com.devikiran.noteapp.data.event.AuthenticationScreenEvent
import com.devikiran.noteapp.data.event.NoteProgressEvent
import com.devikiran.noteapp.data.ui.AuthenticationScreenData
import com.devikiran.noteapp.data.ui.NoteListScreenData
import com.devikiran.noteapp.screens.utils.LoadingState
import com.devikiran.noteapp.screens.utils.NoteRepository
import com.devikiran.noteapp.screens.utils.PreferenceHelper
import com.devikiran.noteapp.screens.utils.Util.isNotValidEmail
import com.devikiran.noteapp.screens.utils.Util.isNotValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationScreenViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {

    private val _authenticationState = MutableStateFlow<AuthenticationScreenData>(AuthenticationScreenData())
    val authenticationState: StateFlow<AuthenticationScreenData> = _authenticationState

    private val _loadingScreenState = MutableStateFlow<LoadingState>(LoadingState.Default)
    val loadingScreenState: StateFlow<LoadingState> = _loadingScreenState

    private var _navigateTo = MutableStateFlow<Any?>(null)
    val navigateTo: StateFlow<Any?> = _navigateTo


    fun onRegistrationValueChange(event: AuthenticationScreenEvent) {
        viewModelScope.launch {
            when (event) {

                is AuthenticationScreenEvent.OnUploadImage -> {}

                is AuthenticationScreenEvent.OnUserName -> {
                    viewModelScope.launch {
                        _authenticationState.update { state ->
                            state.copy(userName = event.userName)
                        }
                    }
                }

                is AuthenticationScreenEvent.OnEmail -> {
                    viewModelScope.launch {
                        _authenticationState.update { state ->
                            state.copy(email = event.email)

                        }
                    }
                }

                is AuthenticationScreenEvent.OnEnterPassword -> {
                    viewModelScope.launch {
                        _authenticationState.update { state ->
                            state.copy(password = event.password)
                        }
                    }
                }

                is AuthenticationScreenEvent.OnReEnterPassword -> {
                    viewModelScope.launch {
                        _authenticationState.update { state ->
                            val isRegister = state.password == event.repeatPassword
                            val passwordMatches = state.password == event.repeatPassword
                            state.copy(
                                rePassword = event.repeatPassword,
                                isRegister = isRegister,
                                isPasswordMatches = passwordMatches
                            )
                        }
                    }
                }

                AuthenticationScreenEvent.OnPasswordVisible -> {
                    viewModelScope.launch {
                        _authenticationState.update { state ->
                            state.copy(isPasswordVisible = !state.isPasswordVisible)
                        }
                    }
                }

                AuthenticationScreenEvent.OnAuthenticate -> {
                    viewModelScope.launch {
                        val errorMsg = validateRegistration()
                        _authenticationState.update { it.copy(errorMessage = errorMsg) }

                        if(errorMsg.isEmpty()){
                            val registerData = _authenticationState.value
                            val register = AuthenticationRequest(
                                userName = registerData.userName,
                                imageUri = "",
                                email = registerData.email,
                                password = registerData.password,
                            )
                            repository.registerUser(authenticationRequest = register, onProcessingEvent = ::handleRegistration)
                        }
                    }
                }

                AuthenticationScreenEvent.OnClearData -> {
                    _authenticationState.update { AuthenticationScreenData() }
                    _loadingScreenState.update { LoadingState.Default }
                }

                AuthenticationScreenEvent.OnClearErrorMsg -> {
                    _authenticationState.update { it.copy(errorMessage = "") }
                }

                is AuthenticationScreenEvent.OnAuthScreen -> {
                    _authenticationState.update { it.copy(selectedPage = event.selectedPage) }
                }

                AuthenticationScreenEvent.OnResetPassword -> {}
                AuthenticationScreenEvent.OnShowForgotPassword -> {}
            }
        }
    }

    fun onLoginValueChange(event: AuthenticationScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is AuthenticationScreenEvent.OnEmail -> {
                    _authenticationState.update {
                        it.copy(email = event.email)
                    }
                }

                is AuthenticationScreenEvent.OnEnterPassword -> {
                    _authenticationState.update {
                        it.copy(password = event.password)
                    }
                }

                AuthenticationScreenEvent.OnPasswordVisible -> {
                    _authenticationState.update {
                        it.copy(isPasswordVisible = !it.isPasswordVisible)
                    }
                }

                AuthenticationScreenEvent.OnAuthenticate -> {
                    val errorMsg = validateLogin()
                    _authenticationState.update { it.copy(errorMessage = errorMsg) }
                    if (errorMsg.isEmpty()) {
                        val loginData = _authenticationState.value
                        val authenticationRequest = AuthenticationRequest(
                            userName = "",
                            imageUri = "",
                            email = loginData.email,
                            password = loginData.password
                        )
                        repository.loginUser(
                            authenticationRequest,
                            onProcessingEvent = ::handleLogin
                        )
                    }
                }
                AuthenticationScreenEvent.OnClearData -> {
                    _authenticationState.update { AuthenticationScreenData() }
                    _loadingScreenState.update { LoadingState.Default }
                }
                AuthenticationScreenEvent.OnShowForgotPassword -> {
                    _authenticationState.update { it.copy(enableForgotPassword = !it.enableForgotPassword) }
                }
                AuthenticationScreenEvent.OnResetPassword -> {
                    val errorMsg = validateForgotPassword()
                    _authenticationState.update { it.copy(errorMessage = errorMsg) }
                    if (errorMsg.isEmpty()) {
                        val authenticationRequest = AuthenticationRequest(
                            userName = _authenticationState.value.userName,
                            email = _authenticationState.value.email,
                            password = _authenticationState.value.password,
                            imageUri = ""
                        )
                        repository.resetPassword(
                            authenticationRequest = authenticationRequest,
                            onProcessingEvent = ::handleResetPassword
                        )
                    }
                }

                is AuthenticationScreenEvent.OnAuthScreen -> {
                    _authenticationState.update { it.copy(selectedPage = event.selectedPage) }
                }

                AuthenticationScreenEvent.OnClearErrorMsg -> {
                    _authenticationState.update { it.copy(errorMessage = "") }
                }
                is AuthenticationScreenEvent.OnReEnterPassword -> {}
                is AuthenticationScreenEvent.OnUploadImage -> {}
                is AuthenticationScreenEvent.OnUserName -> {}

            }
        }
    }

    private fun handleRegistration(processEvent: NoteProgressEvent<String>) {
        viewModelScope.launch {
            when (processEvent) {
                NoteProgressEvent.Completed -> {
                    _loadingScreenState.update { LoadingState.Default }
                }

                is NoteProgressEvent.Fail -> {
                    _loadingScreenState.update { LoadingState.Failure(processEvent.failed) }
                }

                NoteProgressEvent.Progress -> {
                    _loadingScreenState.update { LoadingState.Progress("") }
                }

                NoteProgressEvent.Start -> {
                    _loadingScreenState.update { LoadingState.Loading }
                }

                is NoteProgressEvent.Success<String> -> {
                    _loadingScreenState.update { LoadingState.Success(processEvent.success) }
                    _authenticationState.update { AuthenticationScreenData() }
                }
            }
        }
    }

    private fun handleLogin(processEvent: NoteProgressEvent<User>) {
        viewModelScope.launch {
            when (processEvent) {
                NoteProgressEvent.Completed -> {
                    _loadingScreenState.update { LoadingState.Default }
                }

                is NoteProgressEvent.Fail -> {
                    _loadingScreenState.update { LoadingState.Failure(processEvent.failed) }
                }

                NoteProgressEvent.Progress -> {
                    _loadingScreenState.update { LoadingState.Progress("") }
                }

                NoteProgressEvent.Start -> {
                    _loadingScreenState.update { LoadingState.Loading }
                }

                is NoteProgressEvent.Success<User> -> {
                    _navigateTo.update { NoteListScreenData }
                }
            }
        }
    }

    private fun handleResetPassword(processEvent: NoteProgressEvent<String>) {
        viewModelScope.launch {
            when (processEvent) {
                NoteProgressEvent.Completed -> {
                    _loadingScreenState.update { LoadingState.Default }
                }

                is NoteProgressEvent.Fail -> {
                    _authenticationState.update { AuthenticationScreenData() }
                    _loadingScreenState.update { LoadingState.Failure(processEvent.failed) }
                }

                NoteProgressEvent.Progress -> {
                    _loadingScreenState.update { LoadingState.Progress("") }
                }

                NoteProgressEvent.Start -> {
                    _loadingScreenState.update { LoadingState.Loading }
                }

                is NoteProgressEvent.Success<String> -> {
                    _authenticationState.update { data ->
                        AuthenticationScreenData(email = data.email, password = data.password)
                    }
                }
            }
        }
    }

    private fun validateRegistration(): String {
        val authData = _authenticationState.value
        return if (authData.userName.isBlank()) {
            "Name should not be empty."
        } else if (authData.email.isBlank()) {
            "Email should not be empty."
        } else if (isNotValidEmail(authData.email)) {
            "Invalid email format."
        } else if (authData.password.isBlank()) {
            "Please Enter Password."
        } else if (isNotValidPassword(authData.password)) {
            "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        } else if (authData.password != authData.rePassword) {
            "Please renter password properly."
        } else {
            ""
        }
    }

    private fun validateLogin(): String {
        val authData = _authenticationState.value
        return if (authData.email.isBlank()) {
            "Email should not be empty."
        } else if (isNotValidEmail(authData.email)) {
            "Invalid email format."
        } else if (authData.password.isBlank()) {
            "Please Enter Password."
        } else if (isNotValidPassword(authData.password)) {
            "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        } else {
            ""
        }
    }

    private fun validateForgotPassword(): String {
        val authData = _authenticationState.value
        return if (authData.email.isBlank()) {
            "Email should not be empty."
        } else if (isNotValidEmail(authData.email)) {
            "Invalid email format."
        } else if (authData.password.isBlank()) {
            "Please Enter Password."
        } else if (isNotValidPassword(authData.password)) {
            "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        } else {
            ""
        }
    }
}