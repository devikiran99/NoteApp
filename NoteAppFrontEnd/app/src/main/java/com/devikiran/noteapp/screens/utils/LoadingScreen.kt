package com.devikiran.noteapp.screens.utils


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.devikiran.noteapp.R

@Composable
fun LoadingScreen(
    loadingState: LoadingState
) {
    when (loadingState) {
        LoadingState.Default -> {
            ShowLoadingStateScreen()
        }

        is LoadingState.Failure -> {
            ShowLoadingStateScreen(
                isVisible = true,
                isShowProgressIndicator = false,
                progressText = loadingState.errorMessage,
                textColorId = R.color.red_1
            )
        }

        LoadingState.Loading -> {

            ShowLoadingStateScreen(
                isVisible = true,
                isShowProgressIndicator = true
            )
        }

        is LoadingState.Progress -> {
            ShowLoadingStateScreen(
                isVisible = true,
                isShowProgressIndicator = true
            )
        }

        is LoadingState.Success -> {
            ShowLoadingStateScreen(
                isVisible = true,
                isShowProgressIndicator = false,
                progressText = loadingState.success,
                textColorId = R.color.green_1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowLoadingStateScreen(
    isVisible: Boolean = false,
    isShowProgressIndicator: Boolean = false,
    progressText: String = "",
    textColorId: Int = R.color.transparent
) {
    if(isVisible) {
        BasicAlertDialog(
            onDismissRequest = { }
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (isShowProgressIndicator) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp),
                        color = colorResource(R.color.yellow_1)
                    )
                }

                Text(
                    text = progressText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(textColorId)
                )
            }
        }
    }
}


sealed class LoadingState {
    object Default : LoadingState()
    object Loading : LoadingState()

    data class Progress(val progress: String) : LoadingState()
    data class Failure(val errorMessage: String) : LoadingState()
    data class Success(val success: String) : LoadingState()

}