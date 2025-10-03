package com.devikiran.noteapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.devikiran.noteapp.data.db.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getNoteList(): Flow<List<Note>>

    @Upsert
    suspend fun upsertNoteList(noteList: List<Note>)

    @Delete
    suspend fun deleteNote(note: Note)
}