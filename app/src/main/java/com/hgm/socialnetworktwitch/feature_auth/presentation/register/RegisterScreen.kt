package com.hgm.socialnetworktwitch.feature_auth.presentation.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hgm.socialnetworktwitch.R
import com.hgm.socialnetworktwitch.core.presentation.components.StandardTextField
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.RoundedCornerMedium
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.SpaceMedium
import com.hgm.socialnetworktwitch.core.presentation.util.UiEvent
import com.hgm.socialnetworktwitch.core.util.Constants
import com.hgm.socialnetworktwitch.core.util.autoHideKeyboard
import com.hgm.socialnetworktwitch.feature_auth.util.AuthError
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(
      onNavigateUp: () -> Unit = {},
      navController: NavController,
      snackBarState: SnackbarHostState,
      viewModel: RegisterViewModel = hiltViewModel()
) {
      val emailState = viewModel.emailState.value
      val usernameState = viewModel.usernameState.value
      val passwordState = viewModel.passwordState.value
      val isLoading = viewModel.state.value
      val context = LocalContext.current
      val scope = rememberCoroutineScope()

      LaunchedEffect(key1 = true) {
            viewModel.eventFlow.collectLatest { event ->
                  when (event) {
                        is UiEvent.ShowSnackBar -> {
                              scope.launch {
                                    snackBarState.showSnackbar(event.uiText.asString(context))
                              }
                        }

                        is UiEvent.NavigateUp -> onNavigateUp()
                        else -> Unit
                  }
            }
      }

      Box(
            modifier = Modifier
                  .fillMaxSize()
                  .padding(
                        top = SpaceMedium,
                        start = SpaceMedium,
                        end = SpaceMedium,
                        bottom = 40.dp
                  ),
      ) {
            Column(
                  modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .autoHideKeyboard(),
                  verticalArrangement = Arrangement.Center
            ) {
                  Text(
                        text = stringResource(id = R.string.register),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.displayLarge
                  )
                  Spacer(modifier = Modifier.height(SpaceMedium))
                  StandardTextField(
                        text = emailState.text,
                        hint = stringResource(id = R.string.email),
                        error = when (emailState.error) {
                              AuthError.FieldEmpty -> stringResource(id = R.string.email_cant_be_empty)
                              AuthError.InvalidEmail -> stringResource(id = R.string.email_not_valid)
                              else -> ""
                        },
                        keyboardType = KeyboardType.Email,
                        onValueChange = {
                              viewModel.onEvent(RegisterEvent.EnteredEmail(it))
                        }
                  )
                  Spacer(modifier = Modifier.height(SpaceMedium))
                  StandardTextField(
                        text = usernameState.text,
                        hint = stringResource(id = R.string.username),
                        error = when (usernameState.error) {
                              AuthError.FieldEmpty -> stringResource(id = R.string.username_cant_be_empty)
                              AuthError.FieldTooShort -> stringResource(
                                    id = R.string.username_too_short,
                                    Constants.MIN_USERNAME_LENGTH
                              )

                              else -> ""
                        },
                        onValueChange = {
                              viewModel.onEvent(RegisterEvent.EnteredUsername(it))
                        }
                  )
                  Spacer(modifier = Modifier.height(SpaceMedium))
                  StandardTextField(
                        text = passwordState.text,
                        hint = stringResource(id = R.string.password),
                        keyboardType = KeyboardType.Password,
                        error = when (passwordState.error) {
                              AuthError.FieldEmpty -> stringResource(id = R.string.password_cant_be_empty)
                              AuthError.FieldTooShort -> stringResource(
                                    id = R.string.password_too_short,
                                    Constants.MIN_PASSWORD_LENGTH
                              )

                              AuthError.InvalidPassword -> stringResource(id = R.string.password_not_valid)
                              else -> ""
                        },
                        isShowPassword = passwordState.isPasswordVisible,
                        onValueChange = {
                              viewModel.onEvent(RegisterEvent.EnteredPassword(it))
                        },
                        onPasswordToggleClick = {
                              viewModel.onEvent(RegisterEvent.TogglePasswordVisibility)
                        }
                  )
                  Spacer(modifier = Modifier.height(SpaceMedium))
                  Button(
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(RoundedCornerMedium),
                        enabled = !isLoading,
                        onClick = {
                              viewModel.onEvent(RegisterEvent.Register)
                        }
                  ) {
                        if (isLoading) {
                              CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                              Text(
                                    text = stringResource(id = R.string.register),
                                    color = MaterialTheme.colorScheme.onPrimary
                              )
                        }
                  }
            }

            Text(
                  text = buildAnnotatedString {
                        append(stringResource(id = R.string.already_have_an_account))
                        append(" ")
                        withStyle(
                              style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                              )
                        ) {
                              append(stringResource(id = R.string.sign_in))
                        }
                  },
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .clickable {
                              navController.navigateUp()
                        }
            )
      }

}