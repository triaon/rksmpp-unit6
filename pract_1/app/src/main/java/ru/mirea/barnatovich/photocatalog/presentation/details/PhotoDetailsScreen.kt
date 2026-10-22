package ru.mirea.barnatovich.photocatalog.presentation.details

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.mirea.barnatovich.photocatalog.domain.model.Photo
import ru.mirea.barnatovich.photocatalog.presentation.UiState
import ru.mirea.barnatovich.photocatalog.presentation.common.ErrorView
import ru.mirea.barnatovich.photocatalog.presentation.common.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailsScreen(viewModel: PhotoDetailsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val saving by viewModel.saving.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // SAF: системный диалог «Сохранить как», пользователь сам выбирает папку (Downloads)
    val createDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri -> if (uri != null) viewModel.save(uri.toString()) }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фото") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (val s = state) {
            UiState.Loading -> LoadingView(modifier)
            is UiState.Error -> ErrorView(s.message, onRetry = viewModel::load, modifier = modifier)
            is UiState.Success -> DetailsContent(
                photo = s.data,
                saving = saving,
                onSave = { createDocument.launch("picsum_${s.data.id}.jpg") },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DetailsContent(photo: Photo, saving: Boolean, onSave: () -> Unit, modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = photo.downloadUrl,
            contentDescription = photo.author,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Text(photo.author, style = MaterialTheme.typography.headlineSmall)
        Text("Размер: ${photo.width} × ${photo.height}", style = MaterialTheme.typography.bodyLarge)
        Text("Ссылка:", style = MaterialTheme.typography.labelLarge)
        Text(
            photo.pageUrl,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable { uriHandler.openUri(photo.pageUrl) }
        )
        Button(onClick = onSave, enabled = !saving, modifier = Modifier.fillMaxWidth()) {
            if (saving) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Скачать фото")
            }
        }
    }
}
