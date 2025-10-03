package com.devikiran.noteapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.devikiran.noteapp.database.AppDataBase
import com.devikiran.noteapp.network.ApiService
import com.devikiran.noteapp.screens.utils.NoteRepository
import com.devikiran.noteapp.screens.utils.PreferenceHelper
import com.devikiran.noteapp.screens.utils.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(app: Application): AppDataBase =
        Room.databaseBuilder(app, AppDataBase::class.java, "app_database")
            .build()

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService, appDataBase: AppDataBase, preferenceHelper: PreferenceHelper) =
        NoteRepository(apiService, appDataBase, preferenceHelper)

    @Provides
    @Singleton
    fun provideApiService(): ApiService{
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context) = PreferenceHelper(context)

    @Provides
    @Singleton
    fun providesUserDAo(db: AppDataBase) = db.UserDao()

    @Provides
    @Singleton
    fun providesNoteDAo(db: AppDataBase) = db.NoteDao()
}