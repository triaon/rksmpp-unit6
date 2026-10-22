package ru.mirea.barnatovich.nobel.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.mirea.barnatovich.nobel.AppContainer
import ru.mirea.barnatovich.nobel.domain.model.PrizeCategory
import ru.mirea.barnatovich.nobel.presentation.details.DetailsScreen
import ru.mirea.barnatovich.nobel.presentation.details.DetailsViewModel
import ru.mirea.barnatovich.nobel.presentation.list.LaureatesScreen
import ru.mirea.barnatovich.nobel.presentation.list.LaureatesViewModel

@Serializable
object LaureatesRoute

@Serializable
data class DetailsRoute(val id: String, val year: Int, val categoryCode: String)

@Composable
fun NobelNavHost(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LaureatesRoute) {
        composable<LaureatesRoute> {
            val vm = viewModel { LaureatesViewModel(container.getLaureatesUseCase) }
            LaureatesScreen(vm, onLaureateClick = { laureate ->
                navController.navigate(DetailsRoute(laureate.id, laureate.year, laureate.category.code))
            })
        }
        composable<DetailsRoute> { entry ->
            val route = entry.toRoute<DetailsRoute>()
            val vm = viewModel {
                DetailsViewModel(
                    container.getLaureateDetailsUseCase,
                    route.id,
                    route.year,
                    PrizeCategory.fromCode(route.categoryCode) ?: PrizeCategory.PHYSICS
                )
            }
            DetailsScreen(vm, onBack = { navController.popBackStack() })
        }
    }
}
