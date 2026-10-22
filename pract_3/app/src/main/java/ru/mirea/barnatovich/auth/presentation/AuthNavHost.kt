package ru.mirea.barnatovich.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.mirea.barnatovich.auth.AppContainer
import ru.mirea.barnatovich.auth.presentation.common.LoadingView
import ru.mirea.barnatovich.auth.presentation.details.UserDetailScreen
import ru.mirea.barnatovich.auth.presentation.details.UserDetailViewModel
import ru.mirea.barnatovich.auth.presentation.login.LoginScreen
import ru.mirea.barnatovich.auth.presentation.login.LoginViewModel
import ru.mirea.barnatovich.auth.presentation.users.UsersScreen
import ru.mirea.barnatovich.auth.presentation.users.UsersViewModel

@Serializable
object LoginRoute

@Serializable
object UsersRoute

@Serializable
data class UserDetailRoute(val id: Int)

@Composable
fun AuthNavHost(container: AppContainer) {
    // Пока читаем токен из DataStore, показываем индикатор
    var startDestination by remember { mutableStateOf<Any?>(null) }
    LaunchedEffect(Unit) {
        startDestination = if (container.isLoggedInUseCase()) UsersRoute else LoginRoute
    }
    val start = startDestination ?: return LoadingView()

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = start) {
        composable<LoginRoute> {
            val vm = viewModel { LoginViewModel(container.loginUseCase) }
            LoginScreen(vm, onLoggedIn = {
                navController.navigate(UsersRoute) {
                    popUpTo(LoginRoute) { inclusive = true }
                }
            })
        }
        composable<UsersRoute> {
            val vm = viewModel { UsersViewModel(container.getUsersUseCase) }
            UsersScreen(vm, onUserClick = { navController.navigate(UserDetailRoute(it.id)) })
        }
        composable<UserDetailRoute> { entry ->
            val id = entry.toRoute<UserDetailRoute>().id
            val vm = viewModel { UserDetailViewModel(container.getUserUseCase, container.logoutUseCase, id) }
            UserDetailScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    // Очищаем весь стек, чтобы кнопкой «Назад» нельзя было вернуться
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
