package ru.mirea.barnatovich.photocatalog.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.mirea.barnatovich.photocatalog.domain.model.Photo
import ru.mirea.barnatovich.photocatalog.presentation.UiState
import ru.mirea.barnatovich.photocatalog.presentation.common.ErrorView
import ru.mirea.barnatovich.photocatalog.presentation.common.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(viewModel: PhotosViewModel, onPhotoClick: (Photo) -> Unit) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Фотокаталог") }) }) { padding ->
        val modifier = Modifier.padding(padding)
        when (val s = state) {
            UiState.Loading -> LoadingView(modifier)
            is UiState.Error -> ErrorView(s.message, onRetry = viewModel::load, modifier = modifier)
            is UiState.Success -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier,
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(s.data, key = { it.id }) { photo ->
                    PhotoItem(photo, onClick = { onPhotoClick(photo) })
                }
            }
        }
    }
}

@Composable
private fun PhotoItem(photo: Photo, onClick: () -> Unit) {
    Card(Modifier.clickable(onClick = onClick)) {
        AsyncImage(
            model = photo.thumbnailUrl,
            contentDescription = photo.author,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f)
        )
        Column(Modifier.padding(8.dp)) {
            Text(
                photo.author,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${photo.width} × ${photo.height}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
