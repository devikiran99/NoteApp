package com.devikiran.noteapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devikiran.noteapp.data.db.Note
import com.devikiran.noteapp.data.db.User

@Database(entities = [User::class, Note::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract fun UserDao(): UserDao
    abstract fun NoteDao(): NoteDao
}