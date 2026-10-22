package ru.mirea.barnatovich.nobelclient.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.nobelclient.domain.model.Laureate
import ru.mirea.barnatovich.nobelclient.domain.model.UserProfile
import ru.mirea.barnatovich.nobelclient.domain.usecase.GetFavoritesUseCase
import ru.mirea.barnatovich.nobelclient.domain.usecase.GetProfileUseCase
import ru.mirea.barnatovich.nobelclient.presentation.UiState
import ru.mirea.barnatovich.nobelclient.presentation.toUserMessage

data class FavoritesData(val profile: UserProfile, val laureates: List<Laureate>)

class FavoritesViewModel(
    private val getFavorites: GetFavoritesUseCase,
    private val getProfile: GetProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<FavoritesData>>(UiState.Loading)
    val state: StateFlow<UiState<FavoritesData>> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(FavoritesData(getProfile(), getFavorites()))
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }
}
