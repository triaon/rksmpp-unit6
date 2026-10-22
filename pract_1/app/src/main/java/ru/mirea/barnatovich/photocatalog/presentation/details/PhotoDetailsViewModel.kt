package ru.mirea.barnatovich.photocatalog.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.photocatalog.domain.model.Photo
import ru.mirea.barnatovich.photocatalog.domain.usecase.GetPhotoUseCase
import ru.mirea.barnatovich.photocatalog.domain.usecase.SavePhotoUseCase
import ru.mirea.barnatovich.photocatalog.presentation.UiState
import ru.mirea.barnatovich.photocatalog.presentation.toUserMessage

class PhotoDetailsViewModel(
    private val getPhoto: GetPhotoUseCase,
    private val savePhoto: SavePhotoUseCase,
    private val photoId: String
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Photo>>(UiState.Loading)
    val state: StateFlow<UiState<Photo>> = _state.asStateFlow()

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    // Сообщение для Snackbar после сохранения
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(getPhoto(photoId))
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }

    fun save(destination: String) {
        val photo = (_state.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            _saving.value = true
            _message.value = try {
                savePhoto(photo, destination)
                "Фото сохранено"
            } catch (e: Exception) {
                "Не удалось сохранить: ${e.toUserMessage()}"
            }
            _saving.value = false
        }
    }

    fun messageShown() {
        _message.value = null
    }
}
