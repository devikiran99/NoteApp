package com.devikiran.noteapp.data.event

import com.devikiran.noteapp.data.db.Note

sealed class NoteListScreenEvent {
    data class OnOptionMenuClick(val noteRequestData: Note, val optionMenuData: OptionMenuData) : NoteListScreenEvent()
    data class OnClick(val noteRequestData: Note) : NoteListScreenEvent()
    data class OnLongClick(val noteRequestData: Note) : NoteListScreenEvent()

    object OnReload: NoteListScreenEvent()
    object OnAddNoteData: NoteListScreenEvent()
}

enum class OptionMenuData{
   DEFAULT, EDIT, DELETE
}