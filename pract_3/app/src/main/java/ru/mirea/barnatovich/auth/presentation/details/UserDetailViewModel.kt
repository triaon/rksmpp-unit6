package ru.mirea.barnatovich.auth.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.auth.domain.model.User
import ru.mirea.barnatovich.auth.domain.usecase.GetUserUseCase
import ru.mirea.barnatovich.auth.domain.usecase.LogoutUseCase
import ru.mirea.barnatovich.auth.presentation.UiState
import ru.mirea.barnatovich.auth.presentation.toUserMessage

class UserDetailViewModel(
    private val getUser: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val userId: Int
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<User>>(UiState.Loading)
    val state: StateFlow<UiState<User>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(getUser(userId))
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }

    // Сначала удаляем токен, потом переходим на экран входа
    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onDone()
        }
    }
}
