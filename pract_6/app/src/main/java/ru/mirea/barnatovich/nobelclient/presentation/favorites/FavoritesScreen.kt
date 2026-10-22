package ru.mirea.barnatovich.nobelclient.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mirea.barnatovich.nobelclient.domain.model.Laureate
import ru.mirea.barnatovich.nobelclient.presentation.UiState
import ru.mirea.barnatovich.nobelclient.presentation.common.ErrorView
import ru.mirea.barnatovich.nobelclient.presentation.common.LoadingView
import ru.mirea.barnatovich.nobelclient.presentation.list.LaureateItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onLaureateClick: (Laureate) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // перезагружаем при каждом возврате на экран (после изменений на экране деталей)
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранные премии") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (val s = state) {
            UiState.Loading -> LoadingView(modifier)
            is UiState.Error -> ErrorView(s.message, onRetry = viewModel::load, modifier = modifier)
            is UiState.Success -> Column(modifier.fillMaxSize()) {
                Text(
                    text = "Пользователь: ${s.data.profile.username} · премий в избранном: ${s.data.profile.favoritesCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                if (s.data.laureates.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("В избранном пока ничего нет")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.data.laureates, key = { "${it.id}_${it.prizeId}" }) { laureate ->
                            LaureateItem(laureate, onClick = { onLaureateClick(laureate) })
                        }
                    }
                }
            }
        }
    }
}
