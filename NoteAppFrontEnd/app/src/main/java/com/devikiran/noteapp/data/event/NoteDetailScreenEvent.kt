package com.devikiran.noteapp.data.event

sealed class NoteDetailScreenEvent {
    data class OnNoteTitleChanged(val title: String) : NoteDetailScreenEvent()
    data class OnNoteContentChanged(val content: String) : NoteDetailScreenEvent()
    data object OnUndo : NoteDetailScreenEvent()
    data object OnRedo : NoteDetailScreenEvent()
    data object OnSave : NoteDetailScreenEvent()
    data object OnBackPress : NoteDetailScreenEvent()
}