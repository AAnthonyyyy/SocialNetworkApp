package com.hgm.socialnetworktwitch.feature_auth.presentation.register

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hgm.socialnetworktwitch.R
import com.hgm.socialnetworktwitch.core.domain.states.PasswordTextFieldState
import com.hgm.socialnetworktwitch.core.domain.states.StandardTextFieldState
import com.hgm.socialnetworktwitch.core.util.Constants
import com.hgm.socialnetworktwitch.core.util.Resource
import com.hgm.socialnetworktwitch.core.util.UiText
import com.hgm.socialnetworktwitch.feature_auth.domain.use_case.RegisterUseCase
import com.hgm.socialnetworktwitch.feature_auth.util.AuthError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @auth：HGM
 * @date：2023-09-22 15:54
 * @desc：
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
      private val registerUseCase: RegisterUseCase
) : ViewModel() {
      //邮箱
      private val _emailState = mutableStateOf(StandardTextFieldState())
      val emailState: State<StandardTextFieldState> = _emailState

      //用户名
      private val _usernameState = mutableStateOf(StandardTextFieldState())
      val usernameState: State<StandardTextFieldState> = _usernameState

      //密码
      private val _passwordState = mutableStateOf(PasswordTextFieldState())
      val passwordState: State<PasswordTextFieldState> = _passwordState

      //注册状态
      private val _state = mutableStateOf(RegisterState())
      val state: State<RegisterState> = _state

      //事件流
      private val _eventFlow = MutableSharedFlow<UiEvent>()
      val eventFlow = _eventFlow.asSharedFlow()

      //------------------------------------------------------------------------------------------
      fun onEvent(event: RegisterEvent) {
            when (event) {
                  is RegisterEvent.EnteredEmail -> {
                        _emailState.value = _emailState.value.copy(
                              text = event.value
                        )
                  }

                  is RegisterEvent.EnteredUsername -> {
                        _usernameState.value = _usernameState.value.copy(
                              text = event.value
                        )
                  }

                  is RegisterEvent.EnteredPassword -> {
                        _passwordState.value = _passwordState.value.copy(
                              text = event.value
                        )
                  }

                  is RegisterEvent.TogglePasswordVisibility -> {
                        _passwordState.value = _passwordState.value.copy(
                              isPasswordVisible = !_passwordState.value.isPasswordVisible
                        )
                  }

                  is RegisterEvent.Register -> {
                        validateUsername(usernameState.value.text)
                        validateEmail(emailState.value.text)
                        validatePassword(passwordState.value.text)

                        register()
                  }
            }
      }

      private fun validateUsername(username: String) {
            if (username.trim().isBlank()) {
                  _usernameState.value = _usernameState.value.copy(
                        error = AuthError.FieldEmpty
                  )
                  return
            }

            if (username.trim().length < Constants.MIN_USERNAME_LENGTH) {
                  _usernameState.value = _usernameState.value.copy(
                        error = AuthError.FieldTooShort
                  )
                  return
            }
            _usernameState.value = _usernameState.value.copy(
                  error = null
            )
      }

      private fun validateEmail(email: String) {
            if (email.trim().isBlank()) {
                  _emailState.value = _emailState.value.copy(
                        error = AuthError.FieldEmpty
                  )
                  return
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                  _emailState.value = _emailState.value.copy(
                        error = AuthError.InvalidEmail
                  )
                  return
            }
            _emailState.value = _emailState.value.copy(
                  error = null
            )
      }

      private fun validatePassword(password: String) {
            if (password.trim().isBlank()) {
                  _passwordState.value = _passwordState.value.copy(
                        error = AuthError.FieldEmpty
                  )
                  return
            }

            if (password.trim().length < Constants.MIN_PASSWORD_LENGTH) {
                  _passwordState.value = _passwordState.value.copy(
                        error = AuthError.FieldTooShort
                  )
                  return
            }

            //密码是否包含大写、小写、数字三种字符
            val doesPwdContainUppChar = password.any { it.isUpperCase() }
            val doesPwdContainLowChar = password.any { it.isLowerCase() }
            val doesPwdContainNumChar = password.any { it.isDigit() }
            if (!doesPwdContainUppChar || !doesPwdContainLowChar || !doesPwdContainNumChar) {
                  _passwordState.value = _passwordState.value.copy(
                        error = AuthError.InvalidPassword
                  )
                  return
            }

            _passwordState.value = _passwordState.value.copy(
                  error = null
            )
      }

      private fun register() {
            if (emailState.value.error != null || usernameState.value.error != null || passwordState.value.error != null) {
                  return
            }
            viewModelScope.launch {
                  _state.value = RegisterState(isLoading = true)
                  delay(3000L)
                  val result = registerUseCase(
                        email = emailState.value.text,
                        username = usernameState.value.text,
                        password = passwordState.value.text
                  )
                  when (result) {
                        is Resource.Success -> {
                              _state.value = RegisterState(isLoading = false)
                              _eventFlow.emit(
                                    UiEvent.SnackBarEvent(UiText.StringResource(R.string.register_successful))
                              )
                        }

                        is Resource.Error -> {
                              _state.value = RegisterState(isLoading = false)
                              _eventFlow.emit(
                                    UiEvent.SnackBarEvent(
                                          result.uiText
                                                ?: UiText.StringResource(R.string.error_unknown)
                                    )
                              )
                        }
                  }
            }
      }


      //------------------------------------------------------------------------------------------


      sealed class UiEvent {
            data class SnackBarEvent(val uiText: UiText) : UiEvent()
      }
}