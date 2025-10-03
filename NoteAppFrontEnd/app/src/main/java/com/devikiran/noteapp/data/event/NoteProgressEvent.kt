package com.devikiran.noteapp.data.event

sealed class NoteProgressEvent<out T> {
    data object Start: NoteProgressEvent<Nothing>()
    data object Progress: NoteProgressEvent<Nothing>()
    data class Success<T>(val success: T): NoteProgressEvent<T>()
    data class Fail(val failed: String): NoteProgressEvent<Nothing>()
    data object Completed: NoteProgressEvent<Nothing>()
}