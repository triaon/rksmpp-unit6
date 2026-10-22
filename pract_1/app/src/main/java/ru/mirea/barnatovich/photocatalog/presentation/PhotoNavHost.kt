package ru.mirea.barnatovich.photocatalog.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.mirea.barnatovich.photocatalog.AppContainer
import ru.mirea.barnatovich.photocatalog.presentation.details.PhotoDetailsScreen
import ru.mirea.barnatovich.photocatalog.presentation.details.PhotoDetailsViewModel
import ru.mirea.barnatovich.photocatalog.presentation.list.PhotosScreen
import ru.mirea.barnatovich.photocatalog.presentation.list.PhotosViewModel

@Serializable
object PhotosRoute

@Serializable
data class PhotoDetailsRoute(val id: String)

@Composable
fun PhotoNavHost(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = PhotosRoute) {
        composable<PhotosRoute> {
            val vm = viewModel { PhotosViewModel(container.getPhotosUseCase) }
            PhotosScreen(vm, onPhotoClick = { navController.navigate(PhotoDetailsRoute(it.id)) })
        }
        composable<PhotoDetailsRoute> { entry ->
            val id = entry.toRoute<PhotoDetailsRoute>().id
            val vm = viewModel { PhotoDetailsViewModel(container.getPhotoUseCase, container.savePhotoUseCase, id) }
            PhotoDetailsScreen(vm, onBack = { navController.popBackStack() })
        }
    }
}
