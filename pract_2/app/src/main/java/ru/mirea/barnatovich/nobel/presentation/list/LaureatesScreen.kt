package ru.mirea.barnatovich.nobel.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.mirea.barnatovich.nobel.domain.model.Laureate
import ru.mirea.barnatovich.nobel.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobel.presentation.UiState
import ru.mirea.barnatovich.nobel.presentation.common.ErrorView
import ru.mirea.barnatovich.nobel.presentation.common.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaureatesScreen(viewModel: LaureatesViewModel, onLaureateClick: (Laureate) -> Unit) {
    val state by viewModel.state.collectAsState()
    val filter by viewModel.filter.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Нобелевские лауреаты") }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            FilterPanel(
                filter = filter,
                onYearChange = viewModel::onYearChange,
                onCategoryChange = viewModel::onCategoryChange,
                onApply = viewModel::applyFilter,
                onReset = viewModel::resetFilter
            )
            when (val s = state) {
                UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(s.message, onRetry = viewModel::load)
                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Ничего не найдено")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(s.data, key = { "${it.id}_${it.year}_${it.category.code}" }) { laureate ->
                                LaureateItem(laureate, onClick = { onLaureateClick(laureate) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterPanel(
    filter: FilterState,
    onYearChange: (String) -> Unit,
    onCategoryChange: (PrizeCategory?) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = filter.yearText,
                onValueChange = onYearChange,
                label = { Text("Год") },
                placeholder = { Text("Любой") },
                isError = filter.yearError != null,
                supportingText = filter.yearError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.weight(1.5f)
            ) {
                OutlinedTextField(
                    value = filter.category?.title ?: "Все категории",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    singleLine = true,
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Все категории") },
                        onClick = { onCategoryChange(null); expanded = false }
                    )
                    PrizeCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.title) },
                            onClick = { onCategoryChange(category); expanded = false }
                        )
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onApply, modifier = Modifier.weight(1f)) { Text("Применить") }
            OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) { Text("Сбросить") }
        }
    }
}

@Composable
private fun LaureateItem(laureate: Laureate, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = "${laureate.year} · ${laureate.category.title}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(laureate.fullName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = shortMotivation(laureate.motivation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Первые 100 символов описания
private fun shortMotivation(text: String): String =
    if (text.length <= 100) text else text.take(100).trimEnd() + "…"
