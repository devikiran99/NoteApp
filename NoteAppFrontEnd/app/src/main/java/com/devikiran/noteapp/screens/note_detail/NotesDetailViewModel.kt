package com.devikiran.noteapp.screens.note_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devikiran.noteapp.data.api.NoteRequestData
import com.devikiran.noteapp.data.db.Note
import com.devikiran.noteapp.data.db.User
import com.devikiran.noteapp.data.event.NoteDetailScreenEvent
import com.devikiran.noteapp.data.event.NoteProgressEvent
import com.devikiran.noteapp.data.ui.NoteDetailScreenData
import com.devikiran.noteapp.data.ui.NoteListScreenData
import com.devikiran.noteapp.screens.utils.LoadingState
import com.devikiran.noteapp.screens.utils.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotesDetailViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _noteDetailDataState = MutableStateFlow<NoteDetailScreenData?>(null)
    val noteDetailDataState: StateFlow<NoteDetailScreenData?> = _noteDetailDataState


    private val _loadingScreenState = MutableStateFlow<LoadingState>(LoadingState.Default)
    val loadingScreenState: StateFlow<LoadingState> = _loadingScreenState

    private var noteOldData: NoteDetailScreenData? = null
    private var noteNewData: NoteDetailScreenData? = null

    private var _navigateTo = MutableStateFlow<Any?>(null)
    val navigateTo: StateFlow<Any?> = _navigateTo

    private var user: User? = null

    fun initNoteData(note: Note?) {
        viewModelScope.launch {
            user = repository.getUserList().firstOrNull()
            if (note != null && user != null) {
                val noteDetailData = NoteDetailScreenData(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    createdAt = note.createdAt,
                )
                _noteDetailDataState.update { noteDetailData }
                noteOldData = noteDetailData
            }
        }
    }


    fun onEvent(event: NoteDetailScreenEvent) {
        viewModelScope.launch {
            when (event) {
                NoteDetailScreenEvent.OnBackPress -> {
                    _navigateTo.update { NoteListScreenData }
                }

                is NoteDetailScreenEvent.OnNoteContentChanged -> {
                    viewModelScope.launch {
                        val noteDetailData = _noteDetailDataState.value ?: return@launch
                        _noteDetailDataState.update {
                            val enableSave = event.content != noteOldData?.content
                            noteNewData = noteDetailData.copy(
                                content = event.content,
                                isRedo = false,
                                isUndo = true,
                                isSave = enableSave
                            )
                            noteNewData
                        }
                    }
                }

                is NoteDetailScreenEvent.OnNoteTitleChanged -> {
                    _noteDetailDataState.update {
                        val enableSave = event.title != noteOldData?.title
                        noteNewData = it?.copy(title = event.title, isRedo = false, isUndo = true, isSave = enableSave)
                        noteNewData
                    }
                }

                NoteDetailScreenEvent.OnUndo -> {
                    _noteDetailDataState.update {
                        noteOldData?.copy(isRedo = true, isUndo = false, isSave = false)
                    }
                }

                NoteDetailScreenEvent.OnRedo -> {
                    _noteDetailDataState.update {
                        val enableSave = noteNewData != noteOldData
                        noteNewData = noteNewData?.copy(isRedo = false, isUndo = true, isSave = enableSave)
                        noteNewData
                    }
                }

                NoteDetailScreenEvent.OnSave -> {
                    val noteRequestData = NoteRequestData(
                        id = noteNewData?.id,
                        title = noteNewData!!.title,
                        content = noteNewData!!.content
                    )

                    repository.addNote(noteRequestData, user!!.accessToken, onProcessingEvent = { state ->
                        when(state) {
                            NoteProgressEvent.Completed -> {
                                _loadingScreenState.update { LoadingState.Default }
                            }
                            is NoteProgressEvent.Fail -> {
                                _loadingScreenState.update { LoadingState.Failure(state.failed) }
                            }
                            NoteProgressEvent.Start, NoteProgressEvent.Progress -> {
                                _loadingScreenState.update { LoadingState.Loading }
                            }
                            is NoteProgressEvent.Success<*> -> {
                                _loadingScreenState.update { LoadingState.Success("Successfully Added") }
                                _navigateTo.update { NoteListScreenData }
                            }
                        }
                    })
                }
            }
        }
    }
}