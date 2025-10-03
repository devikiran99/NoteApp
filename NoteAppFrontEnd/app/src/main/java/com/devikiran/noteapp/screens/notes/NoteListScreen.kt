package com.devikiran.noteapp.screens.notes

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.devikiran.assignments.data.event.NoteListScreenActionBarEvent
import com.devikiran.noteapp.R
import com.devikiran.noteapp.data.db.Note
import com.devikiran.noteapp.data.event.NoteListScreenEvent
import com.devikiran.noteapp.data.event.OptionMenuData
import com.devikiran.noteapp.data.ui.ActionBarData
import com.devikiran.noteapp.data.ui.NoteListScreenActionBarData
import com.devikiran.noteapp.screens.utils.LoadingScreen
import com.devikiran.noteapp.screens.utils.LoadingState

@Composable
fun NotesListScreen(
    noteList: List<Note>,
    loadingState: LoadingState,
    onValueChange: (NoteListScreenEvent) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        if (noteList.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.empty_note_list),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(24.dp))


                ElevatedButton(
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = colorResource(R.color.gray_1),
                        contentColor = colorResource(R.color.gray_1),
                        disabledContainerColor = colorResource(R.color.gray_1),
                        disabledContentColor = colorResource(R.color.gray_1),
                    ),
                    onClick = {

                    }
                ) {
                    Text(
                        text = stringResource(R.string.reload),
                        color = colorResource(R.color.white_2),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        } else {
            StaggeredVerticalGrid(
                noteList = noteList
            ) { note ->
                NoteItem(
                    modifier = Modifier.fillMaxWidth(),
                    noteRequestData = note,
                    onValueChange = onValueChange
                )
            }
        }

        LoadingScreen(loadingState = loadingState)


        FloatingActionButton(
            onClick = {
                onValueChange(NoteListScreenEvent.OnAddNoteData)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    noteRequestData: Note,
    onValueChange: (NoteListScreenEvent) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onValueChange(NoteListScreenEvent.OnClick(noteRequestData))
                    },
                    onLongPress = {
                        onValueChange(NoteListScreenEvent.OnLongClick(noteRequestData))
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = noteRequestData.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (noteRequestData.content.length > 100) {
                        noteRequestData.content.take(100) + "..."
                    } else {
                        noteRequestData.content
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box {
                IconButton(onClick = {
                    menuExpanded = true
                }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options"
                    )
                }


                OptionMenu(menuExpanded, onValueChange = { optionData ->
                    menuExpanded = !menuExpanded
                    onValueChange(
                        NoteListScreenEvent.OnOptionMenuClick(
                            noteRequestData = noteRequestData,
                            optionMenuData = optionData
                        )
                    )
                })
            }
        }
    }
}

@Composable
fun StaggeredVerticalGrid(
    noteList: List<Note>,
    itemContent: @Composable (Note) -> Unit
) {
    val numColumns = 2
    val spacing = 8.dp

    Row(
        modifier = Modifier
            .padding(spacing)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        val columns = List(numColumns) { mutableListOf<Note>() }


        noteList.forEachIndexed { index, item ->
            columns[index % numColumns].add(item)
        }

        columns.forEach { columnItems ->
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.weight(1f)
            ) {
                columnItems.forEach { note ->
                    itemContent(note)
                }
            }
        }
    }
}

@Composable
fun OptionMenu(menuExpanded: Boolean, onValueChange: (OptionMenuData) -> Unit) {
    DropdownMenu(
        modifier = Modifier
            .width(96.dp)
            .wrapContentHeight()
            .padding(vertical = 4.dp),
        expanded = menuExpanded,
        offset = DpOffset(x = 0.dp, y = 0.dp),
        onDismissRequest = { onValueChange(OptionMenuData.DEFAULT) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clickable {
                    onValueChange(OptionMenuData.EDIT)
                }
        ) {
            Text(text = stringResource(R.string.edit), style = MaterialTheme.typography.bodyMedium)
        }

        HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clickable {
                    onValueChange(OptionMenuData.DELETE)
                }
        ) {
            Text(
                text = stringResource(R.string.delete),
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(R.color.red_2)
            )
        }
    }
}

fun homeScreenActionBar(
    noteListScreenActionBarData: NoteListScreenActionBarData,
    onValueChange: (NoteListScreenActionBarEvent) -> Unit
) = ActionBarData(
    topBar = {
        Surface(
            color = colorResource(R.color.transparent),
            tonalElevation = 4.dp,
            modifier = Modifier.statusBarsPadding()
        ) {
            if (noteListScreenActionBarData.isShowProfile) {
                ProfileScreenAlertDialogue(
                    noteListScreenActionBarData = noteListScreenActionBarData,
                    onValueChange = onValueChange
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (noteListScreenActionBarData.isEnableSearch) {
                    SearchBar(
                        actionBarData = noteListScreenActionBarData,
                        onValueChange = onValueChange
                    )
                } else {
                    ActionBarScreen(onValueChange = onValueChange)
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = colorResource(R.color.gray_2)
                )
            }
        }
    }
)


@Composable
fun ActionBarScreen(
    onValueChange: (NoteListScreenActionBarEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Notes", color = colorResource(R.color.white_1))

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onValueChange(NoteListScreenActionBarEvent.OnEnableSearch) }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = colorResource(R.color.white_2)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier
                .size(32.dp),
            onClick = {
                onValueChange(NoteListScreenActionBarEvent.OnEnableProfile)
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, colorResource(R.color.white_3), CircleShape)
                    .padding(4.dp),
                contentDescription = null
            )
        }
    }

}

@Composable
fun SearchBar(
    actionBarData: NoteListScreenActionBarData,
    onValueChange: (NoteListScreenActionBarEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(actionBarData.isEnableSearch) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onValueChange(NoteListScreenActionBarEvent.OnEnableSearch) }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
        }

        TextField(
            value = actionBarData.searchData,
            onValueChange = { onValueChange(NoteListScreenActionBarEvent.OnSearch(it)) },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .padding(horizontal = 8.dp),
            placeholder = { Text(stringResource(R.string.search_notes)) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(R.color.gray_3),
                unfocusedContainerColor = colorResource(R.color.gray_3),
                disabledContainerColor = colorResource(R.color.gray_3),
                focusedIndicatorColor = colorResource(R.color.transparent),
                unfocusedIndicatorColor = colorResource(R.color.transparent),
                disabledIndicatorColor = colorResource(R.color.transparent)
            )
        )

        IconButton(
            onClick = {
                focusRequester.requestFocus()
                onValueChange(NoteListScreenActionBarEvent.OnClearSearch)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    }
}
