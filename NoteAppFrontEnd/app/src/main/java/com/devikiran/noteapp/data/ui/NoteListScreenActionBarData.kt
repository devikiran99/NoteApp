package com.devikiran.noteapp.data.ui

import androidx.compose.ui.text.input.TextFieldValue

data class NoteListScreenActionBarData(
    val userName: TextFieldValue = TextFieldValue(""),
    val email: TextFieldValue = TextFieldValue(""),
    val imageUri: String = "",
    val searchData: TextFieldValue = TextFieldValue(""),
    val isEditName: Boolean = false,
    val isEditImage: Boolean = false,
    val isUpdateProfile: Boolean = false,
    val isEnableSearch: Boolean = false,
    val isShowProfile: Boolean = false,
)