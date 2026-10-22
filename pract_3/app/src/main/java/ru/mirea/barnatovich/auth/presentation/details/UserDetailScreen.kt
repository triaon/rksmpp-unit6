package ru.mirea.barnatovich.auth.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.mirea.barnatovich.auth.domain.model.User
import ru.mirea.barnatovich.auth.presentation.UiState
import ru.mirea.barnatovich.auth.presentation.common.ErrorView
import ru.mirea.barnatovich.auth.presentation.common.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(viewModel: UserDetailViewModel, onBack: () -> Unit, onLoggedOut: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
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
            is UiState.Success -> UserContent(
                user = s.data,
                onLogout = { viewModel.logout(onLoggedOut) },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun UserContent(user: User, onLogout: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = user.image,
            contentDescription = user.username,
            modifier = Modifier.size(180.dp).clip(CircleShape)
        )
        Spacer(Modifier.height(8.dp))
        LabeledText("Имя", user.firstName)
        LabeledText("Фамилия", user.lastName)
        LabeledText("Email", user.email)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выйти")
        }
    }
}

@Composable
private fun LabeledText(label: String, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
