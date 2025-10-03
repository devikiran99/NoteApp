package com.devikiran.noteapp.data.api

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequestData(
    val id: String? = null,
    val title: String = "",
    val content: String = ""
)
