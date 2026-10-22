package ru.mirea.barnatovich.nobel.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.nobel.domain.model.LaureateDetails
import ru.mirea.barnatovich.nobel.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobel.domain.usecase.GetLaureateDetailsUseCase
import ru.mirea.barnatovich.nobel.presentation.UiState
import ru.mirea.barnatovich.nobel.presentation.toUserMessage

class DetailsViewModel(
    private val getDetails: GetLaureateDetailsUseCase,
    private val id: String,
    private val year: Int,
    private val category: PrizeCategory
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<LaureateDetails>>(UiState.Loading)
    val state: StateFlow<UiState<LaureateDetails>> = _state.asStateFlow()

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
}
