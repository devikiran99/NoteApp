package com.devikiran.noteapp.data.ui

import androidx.compose.runtime.Composable

data class ActionBarData(
    val topBar: @Composable () -> Unit = {},
    val bottomBar: @Composable () -> Unit = {}
)