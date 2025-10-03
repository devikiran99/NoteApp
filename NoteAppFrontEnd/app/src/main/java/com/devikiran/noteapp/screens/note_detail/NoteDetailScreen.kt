package com.devikiran.noteapp.screens.note_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devikiran.noteapp.R
import com.devikiran.noteapp.data.event.NoteDetailScreenEvent
import com.devikiran.noteapp.data.ui.ActionBarData
import com.devikiran.noteapp.data.ui.NoteDetailScreenData
import com.devikiran.noteapp.screens.utils.LoadingScreen
import com.devikiran.noteapp.screens.utils.LoadingState

@Composable
fun NoteDetailScreen(
    noteDetailData: NoteDetailScreenData,
    loadingState: LoadingState,
    onValueChange: (NoteDetailScreenEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = noteDetailData.title,
                onValueChange = { onValueChange(NoteDetailScreenEvent.OnNoteTitleChanged(it)) },
                shape = RoundedCornerShape(16.dp),
                label = { Text(text = stringResource(R.string.note_title)) },
                placeholder = { Text(text = stringResource(R.string.note_title)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = colorResource(R.color.yellow_1),
                    unfocusedIndicatorColor = colorResource(R.color.gray_2),
                    disabledIndicatorColor = colorResource(R.color.gray_2),
                    cursorColor = colorResource(R.color.white_2)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = noteDetailData.content,
                onValueChange = {  onValueChange(NoteDetailScreenEvent.OnNoteContentChanged(it)) },
                shape = RoundedCornerShape(16.dp),
                label = { Text(text = stringResource(R.string.note_content)) },
                placeholder = { Text(text = stringResource(R.string.note_content)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = colorResource(R.color.yellow_1),
                    unfocusedIndicatorColor = colorResource(R.color.gray_2),
                    disabledIndicatorColor = colorResource(R.color.gray_2),
                    cursorColor = colorResource(R.color.white_2)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = (20.sp.value * 10).dp),
                textStyle = TextStyle(fontSize = 20.sp),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )
        }

        LoadingScreen(loadingState = loadingState)
    }
}

fun noteDetailActionBar(
    noteDetailScreenData: NoteDetailScreenData,
    onValueChange: (NoteDetailScreenEvent) -> Unit
) = ActionBarData(
    topBar = {
        Surface(
            color = colorResource(R.color.transparent),
            tonalElevation = 4.dp,
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
                {
                    IconButton(
                        onClick = {
                            onValueChange(NoteDetailScreenEvent.OnBackPress)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null,
                            tint = colorResource(R.color.white_2)
                        )
                    }

                    Text(
                        text = stringResource(R.string.edit_note),
                        fontSize = 18.sp,
                        color = colorResource(R.color.white_1),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        enabled = noteDetailScreenData.isUndo,
                        onClick = {
                            onValueChange(NoteDetailScreenEvent.OnUndo)

                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_undo),
                            contentDescription = null,
                            tint = colorResource(if (noteDetailScreenData.isUndo) R.color.white_1 else R.color.white_3)
                        )
                    }

                    IconButton(
                        enabled = noteDetailScreenData.isRedo,
                        onClick = {
                            onValueChange(NoteDetailScreenEvent.OnRedo)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_redo),
                            contentDescription = null,
                            tint = colorResource(if (noteDetailScreenData.isRedo) R.color.white_1 else R.color.white_3)
                        )
                    }

                    IconButton(
                        enabled = noteDetailScreenData.isSave,
                        onClick = {
                            onValueChange(NoteDetailScreenEvent.OnSave)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = colorResource(if (noteDetailScreenData.isSave) R.color.yellow_1 else R.color.white_3)
                        )
                    }
                }
            }
        }
    }
)