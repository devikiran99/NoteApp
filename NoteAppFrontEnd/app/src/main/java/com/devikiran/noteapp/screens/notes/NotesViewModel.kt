package com.devikiran.noteapp.screens.notes

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devikiran.assignments.data.event.NoteListScreenActionBarEvent
import com.devikiran.noteapp.data.api.AuthenticationRequest
import com.devikiran.noteapp.data.api.NoteRequestData
import com.devikiran.noteapp.data.db.Note
import com.devikiran.noteapp.data.db.User
import com.devikiran.noteapp.data.event.AddNoteEvent
import com.devikiran.noteapp.data.event.NoteListScreenEvent
import com.devikiran.noteapp.data.event.NoteProgressEvent
import com.devikiran.noteapp.data.event.OptionMenuData
import com.devikiran.noteapp.data.navigation.AuthenticationData
import com.devikiran.noteapp.data.ui.AddNoteScreenData
import com.devikiran.noteapp.data.ui.NoteListScreenActionBarData
import com.devikiran.noteapp.screens.utils.LoadingState
import com.devikiran.noteapp.screens.utils.NoteRepository
import com.devikiran.noteapp.screens.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val pref: PreferenceHelper
) : ViewModel() {
    private val _noteListState = MutableStateFlow<MutableList<Note>>(mutableListOf())
    val noteListState: StateFlow<MutableList<Note>> = _noteListState

    private val noteList = mutableListOf<Note>()

    private val _loadingScreenState = MutableStateFlow<LoadingState>(LoadingState.Default)
    val loadingScreenState: StateFlow<LoadingState> = _loadingScreenState

    private val _noteListScreenActionBarState = MutableStateFlow<NoteListScreenActionBarData>(NoteListScreenActionBarData())
    val noteListScreenActionBarState: StateFlow<NoteListScreenActionBarData> = _noteListScreenActionBarState

    private val _addNoteState = MutableStateFlow<AddNoteScreenData>(AddNoteScreenData())
    val addNoteState: StateFlow<AddNoteScreenData> = _addNoteState

    private var _navigateTo = MutableStateFlow<Any?>(null)
    val navigateTo: StateFlow<Any?> = _navigateTo

    private var user: User? = null

    init {
        viewModelScope.launch {
            user = repository.getUserList().firstOrNull()

            if (user != null) {
                _noteListScreenActionBarState.update {
                    NoteListScreenActionBarData(
                        userName = TextFieldValue(user!!.userName),
                        email = TextFieldValue(user!!.email),
                        imageUri = user!!.imageUri
                    )
                }

                repository.getNoteList().collect { list ->
                    noteList.clear()
                    noteList.addAll(list)
                    _noteListState.update { noteList }
                }
                getNoteListFromServer()
            }
        }
    }

    fun onNoteDataListEvent(event: NoteListScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is NoteListScreenEvent.OnClick -> {
                    _navigateTo.update { event.noteRequestData }
                }

                is NoteListScreenEvent.OnLongClick -> {}
                is NoteListScreenEvent.OnOptionMenuClick -> {
                    handleOptionMenuClick(event.noteRequestData, event.optionMenuData)
                }

                is NoteListScreenEvent.OnAddNoteData -> {
                    _addNoteState.update { it.copy(showScreen = !it.showScreen) }
                }

                NoteListScreenEvent.OnReload -> {
                    getNoteListFromServer()
                }
            }
        }
    }

    fun handleAddNoteValueChange(addNoteEvent: AddNoteEvent) {
        viewModelScope.launch {
            when (addNoteEvent) {
                is AddNoteEvent.OnNoteContentChanged -> {
                    _addNoteState.update { it.copy(content = addNoteEvent.content) }
                }

                is AddNoteEvent.OnNoteTitleChanged -> {
                    _addNoteState.update { it.copy(title = addNoteEvent.title) }
                }

                AddNoteEvent.OnSave -> {
                    val noteData = _addNoteState.value
                    val noteRequestData = NoteRequestData(
                        title = noteData.title,
                        content = noteData.content
                    )

                    repository.addNote(noteRequestData, user!!.accessToken, onProcessingEvent = ::addNoteProcessEvent)

                }

                AddNoteEvent.OnClose -> {
                    _addNoteState.update {
                        it.copy(
                            title = "",
                            content = "",
                            showScreen = !it.showScreen
                        )
                    }
                }
            }
        }
    }

    private fun addNoteProcessEvent(state: NoteProgressEvent<String>) {
        when (state) {

            NoteProgressEvent.Start -> {
                _loadingScreenState.update { LoadingState.Loading }
            }

            is NoteProgressEvent.Success<String> -> {
                _addNoteState.update { it.copy(title = "", content = "") }
                _loadingScreenState.update { LoadingState.Success(state.success) }
            }

            is NoteProgressEvent.Fail -> {
                _loadingScreenState.update { LoadingState.Failure(state.failed) }
            }

            else -> {
                _loadingScreenState.update { LoadingState.Default }
            }
        }
    }

    fun profileScreenEvent(actionBarEvent: NoteListScreenActionBarEvent) {
        viewModelScope.launch {
            when (actionBarEvent) {
                is NoteListScreenActionBarEvent.OnUserName -> {
                    val isUpdateProfile = user?.userName != actionBarEvent.userName.text
                    _noteListScreenActionBarState.update { it.copy(userName = actionBarEvent.userName, isUpdateProfile = isUpdateProfile) }
                }

                is NoteListScreenActionBarEvent.OnImageUri -> {
                    _noteListScreenActionBarState.update { it.copy(imageUri = actionBarEvent.imageUri) }
                }

                NoteListScreenActionBarEvent.OnLogout -> {

                    val data = _noteListScreenActionBarState.value
                    val authenticationRequest = AuthenticationRequest(
                        userName = data.userName.text,
                        imageUri = data.imageUri,
                        email = data.email.text,
                        password = ""
                    )

                    _noteListScreenActionBarState.update {
                        val isShowProfile = !it.isShowProfile
                        it.copy(isShowProfile = isShowProfile)
                    }
                    repository.logoutUser(user = user!!, authenticationRequest = authenticationRequest, onProcessingEvent = ::handleLogout)
                }

                NoteListScreenActionBarEvent.OnUpdateProfile -> {
                    val profile = _noteListScreenActionBarState.value
                    val authRequest = AuthenticationRequest(userName = profile.userName.text, imageUri = "", email = profile.email.text, password = "")
                    repository.updateUserProfile(user = user!!, authRequest = authRequest, onProcessingEvent = ::handleUpdateProfile)
                }

                NoteListScreenActionBarEvent.OnEnableEditName -> {
                    _noteListScreenActionBarState.update {
                        val isEditName = !it.isEditName
                        it.copy(isEditName = isEditName)
                    }
                }

                NoteListScreenActionBarEvent.OnEnableProfile -> {
                    _noteListScreenActionBarState.update {
                        val isShowProfile = !it.isShowProfile
                        it.copy(isShowProfile = isShowProfile)
                    }
                }

                NoteListScreenActionBarEvent.OnClearSearch -> {
                    _noteListScreenActionBarState.update {
                        it.copy(searchData = TextFieldValue(""))
                    }
                    filterNoteList()
                }

                NoteListScreenActionBarEvent.OnEnableSearch -> {
                    _noteListScreenActionBarState.update {
                        val isEnableSearch = !it.isEnableSearch
                        it.copy(isEnableSearch = isEnableSearch, searchData = TextFieldValue(""))
                    }
                    filterNoteList()
                }

                is NoteListScreenActionBarEvent.OnSearch -> {
                    _noteListScreenActionBarState.update { state ->
                        state.copy(searchData = actionBarEvent.query)
                    }

                }
            }
        }
    }

    private fun handleOptionMenuClick(
        note: Note,
        optionMenuData: OptionMenuData
    ) {
        viewModelScope.launch {
            when(optionMenuData) {
                OptionMenuData.DEFAULT -> {}
                OptionMenuData.EDIT -> {
                    _navigateTo.update { note }
                }
                OptionMenuData.DELETE -> {
                    deleteNote(note)
                }
            }
        }
    }

    private suspend fun deleteNote(noteRequestData: Note) {
        repository.deleteNote(note = noteRequestData, accessToken = user!!.accessToken, onProcessingEvent = { state ->
            when(state) {
                NoteProgressEvent.Completed -> {
                    _loadingScreenState.update { LoadingState.Default }
                }
                is NoteProgressEvent.Fail -> {
                    _loadingScreenState.update { LoadingState.Failure(state.failed) }
                }
                NoteProgressEvent.Progress -> {
                    _loadingScreenState.update { LoadingState.Progress("") }
                }
                NoteProgressEvent.Start -> {
                    _loadingScreenState.update { LoadingState.Loading }
                }
                is NoteProgressEvent.Success<String> -> {
                    _loadingScreenState.update { LoadingState.Success(state.success) }
                }
            }
        })
    }

    private fun handleLogout(progressEvent: NoteProgressEvent<String>) {
        viewModelScope.launch {
            when(progressEvent) {
                NoteProgressEvent.Completed -> {
                    _loadingScreenState.update { LoadingState.Default }
                }
                is NoteProgressEvent.Fail -> {
                    _loadingScreenState.update { LoadingState.Failure(progressEvent.failed) }
                }
                NoteProgressEvent.Progress -> {
                    _loadingScreenState.update { LoadingState.Progress("") }
                }
                NoteProgressEvent.Start -> {
                    _loadingScreenState.update { LoadingState.Loading }
                }
                is NoteProgressEvent.Success<String> -> {
                    _loadingScreenState.update {
                        LoadingState.Success(progressEvent.success)
                    }
                    _navigateTo.update { AuthenticationData }
                }
            }
        }
    }

    private fun handleUpdateProfile(progressEvent: NoteProgressEvent<String>) {
        when (progressEvent) {
            NoteProgressEvent.Completed -> {
                _loadingScreenState.update { LoadingState.Default }
            }

            is NoteProgressEvent.Fail -> {
                _loadingScreenState.update { LoadingState.Failure(progressEvent.failed) }
            }

            NoteProgressEvent.Progress -> {
                _loadingScreenState.update { LoadingState.Progress("") }
            }

            NoteProgressEvent.Start -> {
                _loadingScreenState.update { LoadingState.Loading }
            }

            is NoteProgressEvent.Success<String> -> {

                val profile = _noteListScreenActionBarState.value
                user = user?.copy(userName = profile.userName.text, imageUri = profile.imageUri)
                _loadingScreenState.update { LoadingState.Success(progressEvent.success) }

                _noteListScreenActionBarState.update {
                    it.copy(isShowProfile = false)
                }
            }
        }
    }

    private suspend fun getNoteListFromServer() {
        repository.getNotes(user!!.accessToken, onProcessingEvent = { state ->
            when (state) {

                NoteProgressEvent.Completed -> {
                    _loadingScreenState.update { LoadingState.Default }
                }

                is NoteProgressEvent.Fail -> {
                    _loadingScreenState.update { LoadingState.Failure(state.failed) }
                }

                NoteProgressEvent.Progress -> {
                    _loadingScreenState.update { LoadingState.Progress("") }
                }

                NoteProgressEvent.Start -> {
                    _loadingScreenState.update { LoadingState.Loading }
                }

                is NoteProgressEvent.Success<String> -> {
                    _loadingScreenState.update { LoadingState.Success(state.success) }
                }
            }
        })
    }

   private fun filterNoteList() {
        viewModelScope.launch {
            val query = _noteListScreenActionBarState.value.searchData.text
            val newList = noteList.filter { it.title.contains(query, ignoreCase = true) }
            _noteListState.update { newList.toMutableList() }
        }
    }
}