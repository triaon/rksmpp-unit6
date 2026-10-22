package ru.mirea.barnatovich.auth.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.auth.domain.model.User
import ru.mirea.barnatovich.auth.domain.usecase.GetUsersUseCase
import ru.mirea.barnatovich.auth.presentation.UiState
import ru.mirea.barnatovich.auth.presentation.toUserMessage

class UsersViewModel(private val getUsers: GetUsersUseCase) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val state: StateFlow<UiState<List<User>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(getUsers())
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }
}
