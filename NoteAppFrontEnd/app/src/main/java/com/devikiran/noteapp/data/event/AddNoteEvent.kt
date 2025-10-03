package com.devikiran.noteapp.data.event

sealed class AddNoteEvent {
    data class OnNoteTitleChanged(val title: String) : AddNoteEvent()
    data class OnNoteContentChanged(val content: String) : AddNoteEvent()
    object OnClose: AddNoteEvent()

    object OnSave: AddNoteEvent()
}