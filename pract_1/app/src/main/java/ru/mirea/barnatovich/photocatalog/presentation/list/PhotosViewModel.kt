package ru.mirea.barnatovich.photocatalog.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.photocatalog.domain.model.Photo
import ru.mirea.barnatovich.photocatalog.domain.usecase.GetPhotosUseCase
import ru.mirea.barnatovich.photocatalog.presentation.UiState
import ru.mirea.barnatovich.photocatalog.presentation.toUserMessage

class PhotosViewModel(private val getPhotos: GetPhotosUseCase) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Photo>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Photo>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(getPhotos())
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }
}
