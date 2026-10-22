package ru.mirea.barnatovich.nobelclient.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.nobelclient.domain.usecase.LoginUseCase
import ru.mirea.barnatovich.nobelclient.presentation.toUserMessage

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(private val login: LoginUseCase) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onUsernameChange(text: String) = _state.update { it.copy(username = text, error = null) }

    fun onPasswordChange(text: String) = _state.update { it.copy(password = text, error = null) }

    fun submit() {
        val current = _state.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _state.update { it.copy(error = "Введите логин и пароль") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                login(current.username, current.password)
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: Exception) {
                val message = if (e is ClientRequestException && e.response.status.value == 401) {
                    "Неверный логин или пароль"
                } else {
                    e.toUserMessage()
                }
                _state.update { it.copy(isLoading = false, error = message) }
            }
        }
    }
}
