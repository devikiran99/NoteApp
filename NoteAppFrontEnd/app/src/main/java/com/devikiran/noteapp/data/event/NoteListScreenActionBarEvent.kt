package com.devikiran.assignments.data.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class NoteListScreenActionBarEvent {
    data class OnUserName(val userName: TextFieldValue): NoteListScreenActionBarEvent()

    data class OnImageUri(val imageUri: String): NoteListScreenActionBarEvent()

    data class OnSearch(val query: TextFieldValue): NoteListScreenActionBarEvent()
    data object OnEnableProfile : NoteListScreenActionBarEvent()
    data object OnEnableEditName : NoteListScreenActionBarEvent()
    data object OnLogout : NoteListScreenActionBarEvent()
    data object OnUpdateProfile : NoteListScreenActionBarEvent()

    data object OnEnableSearch : NoteListScreenActionBarEvent()

    data object OnClearSearch : NoteListScreenActionBarEvent()
}