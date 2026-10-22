package ru.mirea.barnatovich.nobelclient.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.barnatovich.nobelclient.domain.model.Laureate
import ru.mirea.barnatovich.nobelclient.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobelclient.domain.usecase.GetLaureatesUseCase
import ru.mirea.barnatovich.nobelclient.presentation.UiState
import ru.mirea.barnatovich.nobelclient.presentation.toUserMessage
import java.time.Year

data class FilterState(
    val yearText: String = "",
    val category: PrizeCategory? = null,
    val yearError: String? = null
)

class LaureatesViewModel(private val getLaureates: GetLaureatesUseCase) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Laureate>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Laureate>>> = _state.asStateFlow()

    private val _filter = MutableStateFlow(FilterState())
    val filter: StateFlow<FilterState> = _filter.asStateFlow()

    // Фильтр, с которым был сделан последний запрос (нужен для «Повторить»)
    private var appliedYear: Int? = null
    private var appliedCategory: PrizeCategory? = null

    init {
        load()
    }

    fun onYearChange(text: String) {
        _filter.update { it.copy(yearText = text.filter(Char::isDigit).take(4), yearError = null) }
    }

    fun onCategoryChange(category: PrizeCategory?) {
        _filter.update { it.copy(category = category) }
    }

    fun applyFilter() {
        val current = _filter.value
        val lastYear = Year.now().value
        val year = current.yearText.toIntOrNull()
        if (current.yearText.isNotEmpty() && (year == null || year !in FIRST_YEAR..lastYear)) {
            _filter.update { it.copy(yearError = "Год от $FIRST_YEAR до $lastYear") }
            return
        }
        appliedYear = year
        appliedCategory = current.category
        load()
    }

    fun resetFilter() {
        _filter.value = FilterState()
        appliedYear = null
        appliedCategory = null
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(getLaureates(appliedYear, appliedCategory))
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage())
            }
        }
    }

    companion object {
        const val FIRST_YEAR = 1901
    }
}
