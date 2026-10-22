package ru.mirea.barnatovich.nobel.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.mirea.barnatovich.nobel.domain.model.LaureateDetails
import ru.mirea.barnatovich.nobel.presentation.UiState
import ru.mirea.barnatovich.nobel.presentation.common.ErrorView
import ru.mirea.barnatovich.nobel.presentation.common.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(viewModel: DetailsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Лауреат") },
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
            is UiState.Success -> DetailsContent(s.data, modifier)
        }
    }
}

@Composable
private fun DetailsContent(details: LaureateDetails, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (details.photoUrl != null) {
            AsyncImage(
                model = details.photoUrl,
                contentDescription = details.fullName,
                modifier = Modifier
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            Text(
                "Фото отсутствует",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Text(details.fullName, style = MaterialTheme.typography.headlineSmall)
        InfoRow("Год и категория", "${details.year}, ${details.category.title}")
        InfoRow("Страна", details.birthCountry ?: "нет данных")
        InfoRow("Место рождения", details.birthPlace ?: "нет данных")
        InfoRow("За что присуждена премия", details.motivation.ifEmpty { "нет данных" })
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
