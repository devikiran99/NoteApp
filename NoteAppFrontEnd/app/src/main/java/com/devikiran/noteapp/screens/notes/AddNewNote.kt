package com.devikiran.noteapp.screens.notes

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devikiran.noteapp.R
import com.devikiran.noteapp.data.event.AddNoteEvent
import com.devikiran.noteapp.data.ui.AddNoteScreenData
import com.devikiran.noteapp.screens.utils.LoadingScreen
import com.devikiran.noteapp.screens.utils.LoadingState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    noteData: AddNoteScreenData,
    loadingState: LoadingState,
    onValueChange: (AddNoteEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (noteData.showScreen) {

        ModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            onDismissRequest = {
                onValueChange(AddNoteEvent.OnClose)
            },
            sheetState = sheetState,
            dragHandle = null
        ) {


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                onValueChange(AddNoteEvent.OnClose)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(text = stringResource(R.string.note_title), modifier = Modifier.weight(1f))

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = colorResource(R.color.gray_2)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = noteData.title,
                        onValueChange = { onValueChange(AddNoteEvent.OnNoteTitleChanged(it)) },
                        shape = RoundedCornerShape(16.dp),
                        label = { Text(text = stringResource(R.string.note_title)) },
                        placeholder = { Text(text = stringResource(R.string.dummy_note_title)) },
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
                        value = noteData.content,
                        onValueChange = { onValueChange(AddNoteEvent.OnNoteContentChanged(it)) },
                        shape = RoundedCornerShape(16.dp),
                        label = { Text(text = stringResource(R.string.note_content)) },
                        placeholder = { Text(text = stringResource(R.string.dummy_note_content)) },
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onValueChange(AddNoteEvent.OnSave)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.note_title)
                        )
                    }
                }

                LoadingScreen(loadingState = loadingState)
            }

        }
    }
}