package com.devikiran.noteapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.devikiran.noteapp.data.db.Note
import com.devikiran.noteapp.data.navigation.AuthenticationData
import com.devikiran.noteapp.data.navigation.HolderData
import com.devikiran.noteapp.data.ui.ActionBarData
import com.devikiran.noteapp.data.ui.NoteListScreenData
import com.devikiran.noteapp.screens.authenticate.AuthenticationScreen
import com.devikiran.noteapp.screens.authenticate.AuthenticationScreenViewModel
import com.devikiran.noteapp.screens.holder.HolderScreen
import com.devikiran.noteapp.screens.holder.HolderViewModel
import com.devikiran.noteapp.screens.note_detail.NoteDetailScreen
import com.devikiran.noteapp.screens.note_detail.NotesDetailViewModel
import com.devikiran.noteapp.screens.note_detail.noteDetailActionBar
import com.devikiran.noteapp.screens.notes.AddNoteScreen
import com.devikiran.noteapp.screens.notes.NotesListScreen
import com.devikiran.noteapp.screens.notes.NotesViewModel
import com.devikiran.noteapp.screens.notes.homeScreenActionBar

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    var actionBarData by remember { mutableStateOf(ActionBarData()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { actionBarData.topBar() },
        bottomBar = { actionBarData.bottomBar() }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HolderData,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable<HolderData> {
                val holderViewModel: HolderViewModel = hiltViewModel()

                LaunchedEffect(holderViewModel.navigateTo) {

                    holderViewModel.navigateTo.collect {
                        if (it != null) {
                            navController.navigate(it)
                        }
                    }
                }

                HolderScreen()
            }

            composable<AuthenticationData> {

                val registrationViewModel: AuthenticationScreenViewModel = hiltViewModel()

                val authState = registrationViewModel.authenticationState.collectAsState()

                val loadingState =registrationViewModel.loadingScreenState.collectAsState()


                LaunchedEffect(registrationViewModel.navigateTo) {
                    registrationViewModel.navigateTo.collect {
                        if (it != null) {
                            navController.navigate(it)
                        }
                    }
                }

                AuthenticationScreen(
                    authState = authState.value,
                    loadingState = loadingState.value,
                    onRegisterValueChange = { registrationViewModel.onRegistrationValueChange(it) },
                    onLoginValueChange = { registrationViewModel.onLoginValueChange(it) },
                )
            }

            //Note list Screen
            composable<NoteListScreenData> {

                val notesVewModel: NotesViewModel = hiltViewModel()

                val noteListScreenActionBarState = notesVewModel.noteListScreenActionBarState.collectAsState()

                val addNoteState = notesVewModel.addNoteState.collectAsState()

                val noteDataList = notesVewModel.noteListState.collectAsState()

                val loadingState = notesVewModel.loadingScreenState.collectAsState()

                actionBarData = homeScreenActionBar(
                    noteListScreenActionBarData = noteListScreenActionBarState.value,
                    onValueChange = { notesVewModel.profileScreenEvent(it) }
                )

                LaunchedEffect(notesVewModel.navigateTo) {
                    notesVewModel.navigateTo.collect {
                        if (it != null) {
                            navController.navigate(it)
                        }
                    }
                }

                NotesListScreen(
                    noteList = noteDataList.value,
                    loadingState = loadingState.value,
                    onValueChange = { notesVewModel.onNoteDataListEvent(it) }
                )

                AddNoteScreen(
                    noteData = addNoteState.value,
                    loadingState = loadingState.value,
                    onValueChange = { notesVewModel.handleAddNoteValueChange(it)}
                )
            }

            //Note Detail Screen
            composable<Note> {
                val noteRequestData = it.toRoute<Note>()
                val noteDetailVewModel: NotesDetailViewModel = hiltViewModel()

                LaunchedEffect(Unit) {
                    noteDetailVewModel.initNoteData(noteRequestData)
                }

                LaunchedEffect(noteDetailVewModel.navigateTo) {
                    noteDetailVewModel.navigateTo.collect { navData ->
                        if (navData != null) {
                            navController.navigate(navData)
                        }
                    }
                }

                val noteDetailData = noteDetailVewModel.noteDetailDataState.collectAsState()
                val loadingState = noteDetailVewModel.loadingScreenState.collectAsState()

                if (noteDetailData.value != null) {
                    actionBarData = noteDetailActionBar(
                        noteDetailScreenData = noteDetailData.value!!,
                        onValueChange = {
                            noteDetailVewModel.onEvent(it)
                        }
                    )
                    NoteDetailScreen(
                        noteDetailData = noteDetailData.value!!,
                        loadingState = loadingState.value,
                        onValueChange = {
                            noteDetailVewModel.onEvent(it)
                        }
                    )
                }
            }
        }
    }
}