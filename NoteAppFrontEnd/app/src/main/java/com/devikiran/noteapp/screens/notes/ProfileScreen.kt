package com.devikiran.noteapp.screens.notes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devikiran.assignments.data.event.NoteListScreenActionBarEvent
import com.devikiran.noteapp.R
import com.devikiran.noteapp.data.ui.NoteListScreenActionBarData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenAlertDialogue(
    noteListScreenActionBarData: NoteListScreenActionBarData?,
    onValueChange: (NoteListScreenActionBarEvent) -> Unit
) {

    BasicAlertDialog(
        onDismissRequest = { }
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp,
            color = colorResource(R.color.gray_4),
            modifier = Modifier
                .wrapContentSize()
                .padding(24.dp)
        ) {
            ProfileScreen(noteListScreenActionBarData = noteListScreenActionBarData, onValueChange = onValueChange)
        }
    }
}

@Composable
fun ProfileScreen(
    noteListScreenActionBarData: NoteListScreenActionBarData?,
    onValueChange: (NoteListScreenActionBarEvent) -> Unit
) {
    Box {
        if (noteListScreenActionBarData == null) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.size(80.dp)
            ) {
                Text(text = stringResource(R.string.profile_error))
            }

        } else {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 24.dp, bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(32.dp))
                        .border(2.dp, colorResource(R.color.white_2), CircleShape)
                        .padding(8.dp),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.size(8.dp))

                TextField(
                    value = noteListScreenActionBarData.userName,
                    onValueChange = {
                        if (noteListScreenActionBarData.isEditName) {
                            onValueChange(NoteListScreenActionBarEvent.OnUserName(it))
                        }
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.user_name),
                            color = colorResource(R.color.white_3),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif
                        )
                    },
                    readOnly = !noteListScreenActionBarData.isEditName,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { onValueChange(NoteListScreenActionBarEvent.OnEnableEditName) }) {
                            Icon(
                                imageVector = if (noteListScreenActionBarData.isEditName) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = null,
                                tint = colorResource(R.color.white_3)
                            )
                        }
                    },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            color = colorResource(R.color.white_3),
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = noteListScreenActionBarData.email.text,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            color = colorResource(R.color.white_3),
                            shape = RoundedCornerShape( 16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.size(32.dp))

                Button(
                    onClick = { onValueChange(NoteListScreenActionBarEvent.OnUpdateProfile) },
                    enabled = noteListScreenActionBarData.isUpdateProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.black_3),
                        contentColor = Color.Black
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.update),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        color = if(noteListScreenActionBarData.isUpdateProfile) { colorResource(R.color.yellow_1) }else { colorResource(R.color.yellow_3) },
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    onClick = { onValueChange(NoteListScreenActionBarEvent.OnLogout) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.black_3),
                        contentColor = Color.Black
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.logout),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        color = colorResource(R.color.red_2),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        IconButton(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopStart),
            onClick = { onValueChange(NoteListScreenActionBarEvent.OnEnableProfile) }
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = colorResource(R.color.red_1)
            )
        }
    }
}