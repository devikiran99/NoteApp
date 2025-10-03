package com.devikiran.noteapp.screens.authenticate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devikiran.noteapp.R
import com.devikiran.noteapp.data.event.AuthenticationScreenEvent
import com.devikiran.noteapp.data.ui.AuthenticationScreenData
import com.devikiran.noteapp.screens.utils.LoadingScreen
import com.devikiran.noteapp.screens.utils.LoadingState


@Composable
fun AuthenticationScreen(
    authState: AuthenticationScreenData,
    loadingState: LoadingState,
    onRegisterValueChange: (AuthenticationScreenEvent) -> Unit,
    onLoginValueChange: (AuthenticationScreenEvent) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = authState.selectedPage, pageCount = { 2 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16))
                .background(color = colorResource(R.color.gray_1))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name_to_display),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                color = Color.White
            )

            Text(
                text = stringResource(R.string.app_description),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = Color.White
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(50))
                .background(colorResource(R.color.gray_1))

        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (authState.selectedPage == 0) colorResource(R.color.yellow_2) else colorResource(
                            R.color.transparent
                        )
                    )
                    .clickable {
                        onLoginValueChange(AuthenticationScreenEvent.OnClearData)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.login),
                    color = colorResource(R.color.white_2),
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (authState.selectedPage == 1) colorResource(R.color.yellow_2) else colorResource(
                            R.color.transparent
                        )
                    )
                    .clickable {
                        onRegisterValueChange(AuthenticationScreenEvent.OnClearData)
                        onRegisterValueChange(AuthenticationScreenEvent.OnAuthScreen(1))
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.register),
                    color = colorResource(R.color.white_2),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState, modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> LoginScreen(
                    authState = authState,
                    loadingState = loadingState,
                    onValueChange = onLoginValueChange
                )

                1 -> RegisterScreen(
                    authState = authState,
                    loadingState = loadingState,
                    onValueChange = onRegisterValueChange
                )
            }
        }

        LaunchedEffect(authState.selectedPage) {
            pagerState.animateScrollToPage(authState.selectedPage)
        }
        LaunchedEffect(pagerState.currentPage) {
            onRegisterValueChange(AuthenticationScreenEvent.OnAuthScreen(pagerState.currentPage))
        }
    }
}

@Composable
fun RegisterScreen(
    authState: AuthenticationScreenData,
    loadingState: LoadingState,
    onValueChange: (AuthenticationScreenEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            OutlinedTextField(
                value = authState.userName,
                onValueChange = { onValueChange(AuthenticationScreenEvent.OnUserName(it)) },
                shape = RoundedCornerShape(16.dp),
                label = { Text(text = stringResource(R.string.user_name)) },
                placeholder = { Text(text = stringResource(R.string.dummy_user_name)) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = authState.email,
                onValueChange = { onValueChange(AuthenticationScreenEvent.OnEmail(it)) },
                shape = RoundedCornerShape(16.dp),
                label = { Text(text = stringResource(R.string.user_email)) },
                placeholder = { Text(text = stringResource(R.string.dummy_user_email)) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = authState.password,
                onValueChange = { onValueChange(AuthenticationScreenEvent.OnEnterPassword(it)) },
                label = { Text(text = stringResource(R.string.set_user_password)) },
                placeholder = { Text(text = stringResource(R.string.dummy_user_password)) },
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (authState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (authState.isPasswordVisible) painterResource(R.drawable.ic_eye)
                    else painterResource(R.drawable.ic_eye_off)

                    IconButton(
                        onClick = { onValueChange(AuthenticationScreenEvent.OnPasswordVisible) }) {
                        Icon(
                            painter = image,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.white_3)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = authState.rePassword,
                onValueChange = { onValueChange(AuthenticationScreenEvent.OnReEnterPassword(it)) },
                label = { Text(text = stringResource(R.string.reset_user_password)) },
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (authState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {

                    var image = painterResource(R.drawable.ic_blank)
                    var imageTint = colorResource(R.color.transparent)

                    if (authState.isPasswordMatches) {
                        image = painterResource(R.drawable.ic_check_small)
                        imageTint = colorResource(R.color.green_1)
                    } else if (authState.isPasswordVisible) {
                        image =painterResource(R.drawable.ic_eye)
                        imageTint = colorResource(R.color.white_3)
                    } else {
                        image = painterResource(R.drawable.ic_eye_off)
                        imageTint = colorResource(R.color.white_3)
                    }

                    IconButton(
                        onClick = {
                            if (!authState.isPasswordMatches) {
                                onValueChange(AuthenticationScreenEvent.OnPasswordVisible)
                            }
                        }) {
                        Icon(
                            painter = image,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = imageTint
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthValidationScreen(authState = authState, onValueChange = onValueChange)

            ElevatedButton(
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorResource(R.color.gray_1),
                    contentColor = colorResource(R.color.gray_1),
                    disabledContainerColor = colorResource(R.color.gray_1),
                    disabledContentColor = colorResource(R.color.gray_1),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    focusManager.clearFocus()
                    onValueChange(AuthenticationScreenEvent.OnAuthenticate)
                }
            ) {
                Text(
                    text = stringResource(R.string.register),
                    color = colorResource(R.color.white_2),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        LoadingScreen(loadingState)
    }
}

@Composable
fun LoginScreen(
    authState: AuthenticationScreenData,
    loadingState: LoadingState,
    onValueChange: (AuthenticationScreenEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            OutlinedTextField(
                value = authState.email,
                onValueChange = { onValueChange(AuthenticationScreenEvent.OnEmail(it)) },
                label = { Text(stringResource(R.string.user_email)) },
                placeholder = { Text(stringResource(R.string.dummy_user_email)) },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = authState.password,
                onValueChange = { onValueChange(AuthenticationScreenEvent.OnEnterPassword(it)) },
                label = { Text(stringResource(R.string.set_user_password)) },
                placeholder = { Text(stringResource(R.string.dummy_user_password)) },
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (authState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (authState.isPasswordVisible) painterResource(R.drawable.ic_eye)
                    else painterResource(R.drawable.ic_eye_off)

                    IconButton(onClick = {
                        onValueChange(AuthenticationScreenEvent.OnPasswordVisible)
                    }) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.white_3)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthValidationScreen(authState = authState, onValueChange = onValueChange)

            ElevatedButton(
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorResource(R.color.gray_1),
                    contentColor = colorResource(R.color.gray_1),
                    disabledContainerColor = colorResource(R.color.gray_1),
                    disabledContentColor = colorResource(R.color.gray_1),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    focusManager.clearFocus()
                    onValueChange(AuthenticationScreenEvent.OnAuthenticate)
                }
            ) {
                Text(
                    text = stringResource(R.string.login),
                    color = colorResource(R.color.white_2),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.forgot_password),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        focusManager.clearFocus()
                        onValueChange(AuthenticationScreenEvent.OnShowForgotPassword)
                    },
                color = colorResource(R.color.blue_1),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline
                )
            )
        }
        LoadingScreen(loadingState)

        ForgotPassword(authState = authState, onValueChange = onValueChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassword(
    authState: AuthenticationScreenData,
    onValueChange: (AuthenticationScreenEvent) -> Unit
) {
    if (authState.enableForgotPassword) {
        BasicAlertDialog(
            onDismissRequest = { }
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 8.dp,
                color = colorResource(R.color.gray_4)
            ) {

                val focusManager = LocalFocusManager.current

                Box(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            IconButton(onClick = {
                                onValueChange(AuthenticationScreenEvent.OnShowForgotPassword)
                            }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(R.string.reset_user_password),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 16.dp),
                            thickness = 2.dp,
                            color = colorResource(R.color.white_3)
                        )

                        OutlinedTextField(
                            value = authState.email,
                            onValueChange = { onValueChange(AuthenticationScreenEvent.OnEmail(it)) },
                            label = { Text(stringResource(R.string.user_email)) },
                            placeholder = { Text(stringResource(R.string.dummy_user_email)) },
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = authState.password,
                            onValueChange = {
                                onValueChange(
                                    AuthenticationScreenEvent.OnEnterPassword(
                                        it
                                    )
                                )
                            },
                            label = { Text(stringResource(R.string.set_user_password)) },
                            placeholder = { Text(stringResource(R.string.dummy_user_password)) },
                            shape = RoundedCornerShape(16.dp),
                            visualTransformation = if (authState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val icon =
                                    if (authState.isPasswordVisible) painterResource(R.drawable.ic_eye)
                                    else painterResource(R.drawable.ic_eye_off)

                                IconButton(onClick = {
                                    onValueChange(AuthenticationScreenEvent.OnPasswordVisible)
                                }) {
                                    Icon(
                                        painter = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        AuthValidationScreen(authState = authState, onValueChange = onValueChange)

                        ElevatedButton(
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = colorResource(R.color.black_3),
                                contentColor = colorResource(R.color.black_3),
                                disabledContainerColor = colorResource(R.color.black_3),
                                disabledContentColor = colorResource(R.color.black_3),
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            onClick = {
                                focusManager.clearFocus()
                                onValueChange(AuthenticationScreenEvent.OnResetPassword)
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.reset),
                                color = colorResource(R.color.white_2),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthValidationScreen(
    authState: AuthenticationScreenData,
    onValueChange: (AuthenticationScreenEvent) -> Unit
) {
    if (authState.errorMessage.isNotBlank()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = colorResource(R.color.gray_2))
                .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 8.dp)

        ) {
            Text(
                text = authState.errorMessage,
                color = colorResource(R.color.red_1),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = {
                    onValueChange(
                        AuthenticationScreenEvent.OnClearErrorMsg
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = colorResource(R.color.white_2)
                )
            }

        }
    }
}