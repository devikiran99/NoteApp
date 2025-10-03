package com.devikiran.noteapp.data.ui

data class NoteDetailScreenData(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: String = "",
    val isRedo: Boolean = false,
    val isUndo: Boolean = false,
    val isSave: Boolean = false
)