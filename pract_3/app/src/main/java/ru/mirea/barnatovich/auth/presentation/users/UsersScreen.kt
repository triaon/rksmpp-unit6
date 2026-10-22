package ru.mirea.barnatovich.auth.presentation.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun UsersScreen(viewModel: UsersViewModel, onUserClick: (User) -> Unit) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Пользователи") }) }) { padding ->
        val modifier = Modifier.padding(padding)
        when (val s = state) {
            UiState.Loading -> LoadingView(modifier)
            is UiState.Error -> ErrorView(s.message, onRetry = viewModel::load, modifier = modifier)
            is UiState.Success -> LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(s.data, key = { it.id }) { user ->
                    UserItem(user, onClick = { onUserClick(user) })
                }
            }
        }
    }
}

@Composable
private fun UserItem(user: User, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = user.image,
                contentDescription = user.username,
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
            Column {
                Text("${user.firstName} ${user.lastName}", style = MaterialTheme.typography.titleMedium)
                Text("@${user.username}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
