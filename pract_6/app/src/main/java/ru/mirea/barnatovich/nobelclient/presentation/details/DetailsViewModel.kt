package ru.mirea.barnatovich.nobelclient.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.nobelclient.domain.model.LaureateDetails
import ru.mirea.barnatovich.nobelclient.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobelclient.domain.usecase.GetLaureateDetailsUseCase
import ru.mirea.barnatovich.nobelclient.domain.usecase.ToggleFavoriteUseCase
import ru.mirea.barnatovich.nobelclient.presentation.UiState
import ru.mirea.barnatovich.nobelclient.presentation.toUserMessage

class DetailsViewModel(
    private val getDetails: GetLaureateDetailsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val id: String,
    private val year: Int,
    private val category: PrizeCategory
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<LaureateDetails>>(UiState.Loading)
    val state: StateFlow<UiState<LaureateDetails>> = _state.asStateFlow()

    // сообщение об ошибке при добавлении/удалении из избранного
    private val _favoriteError = MutableStateFlow<String?>(null)
    val favoriteError: StateFlow<String?> = _favoriteError.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(getDetails(id, year, category))
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }

    fun onFavoriteClick() {
        val details = (_state.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            try {
                val nowFavorite = toggleFavorite(details.prizeId, details.isFavorite)
                _state.value = UiState.Success(details.copy(isFavorite = nowFavorite))
                _favoriteError.value = null
            } catch (e: Exception) {
                _favoriteError.value = e.toUserMessage()
            }
        }
    }
}
