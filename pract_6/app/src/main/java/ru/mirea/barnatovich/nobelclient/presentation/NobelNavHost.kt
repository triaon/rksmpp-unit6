package ru.mirea.barnatovich.nobelclient.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.mirea.barnatovich.nobelclient.AppContainer
import ru.mirea.barnatovich.nobelclient.domain.model.Laureate
import ru.mirea.barnatovich.nobelclient.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobelclient.presentation.details.DetailsScreen
import ru.mirea.barnatovich.nobelclient.presentation.details.DetailsViewModel
import ru.mirea.barnatovich.nobelclient.presentation.favorites.FavoritesScreen
import ru.mirea.barnatovich.nobelclient.presentation.favorites.FavoritesViewModel
import ru.mirea.barnatovich.nobelclient.presentation.list.LaureatesScreen
import ru.mirea.barnatovich.nobelclient.presentation.list.LaureatesViewModel
import ru.mirea.barnatovich.nobelclient.presentation.login.LoginScreen
import ru.mirea.barnatovich.nobelclient.presentation.login.LoginViewModel

@Serializable
object LoginRoute

@Serializable
object LaureatesRoute

@Serializable
object FavoritesRoute

@Serializable
data class DetailsRoute(val id: String, val year: Int, val categoryCode: String)

@Composable
fun NobelNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val openDetails = { laureate: Laureate ->
        navController.navigate(DetailsRoute(laureate.id, laureate.year, laureate.category.code))
    }

    NavHost(navController = navController, startDestination = LoginRoute) {
        composable<LoginRoute> {
            val vm = viewModel { LoginViewModel(container.loginUseCase) }
            LoginScreen(vm, onLoggedIn = {
                navController.navigate(LaureatesRoute) {
                    popUpTo(LoginRoute) { inclusive = true }
                }
            })
        }
        composable<LaureatesRoute> {
            val vm = viewModel { LaureatesViewModel(container.getLaureatesUseCase) }
            LaureatesScreen(
                vm,
                onLaureateClick = openDetails,
                onFavoritesClick = { navController.navigate(FavoritesRoute) },
                onLogout = {
                    container.logoutUseCase()
                    navController.navigate(LoginRoute) {
                        popUpTo(LaureatesRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<FavoritesRoute> {
            val vm = viewModel { FavoritesViewModel(container.getFavoritesUseCase, container.getProfileUseCase) }
            FavoritesScreen(vm, onLaureateClick = openDetails, onBack = { navController.popBackStack() })
        }
        composable<DetailsRoute> { entry ->
            val route = entry.toRoute<DetailsRoute>()
            val vm = viewModel {
                DetailsViewModel(
                    container.getLaureateDetailsUseCase,
                    container.toggleFavoriteUseCase,
                    route.id,
                    route.year,
                    PrizeCategory.fromCode(route.categoryCode) ?: PrizeCategory.PHYSICS
                )
            }
            DetailsScreen(vm, onBack = { navController.popBackStack() })
        }
    }
}
