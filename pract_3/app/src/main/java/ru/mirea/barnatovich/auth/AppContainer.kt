package ru.mirea.barnatovich.auth

import android.content.Context
import ru.mirea.barnatovich.auth.data.local.TokenStorage
import ru.mirea.barnatovich.auth.data.remote.DummyJsonApi
import ru.mirea.barnatovich.auth.data.remote.createHttpClient
import ru.mirea.barnatovich.auth.data.repository.AuthRepositoryImpl
import ru.mirea.barnatovich.auth.data.repository.UserRepositoryImpl
import ru.mirea.barnatovich.auth.domain.usecase.GetUserUseCase
import ru.mirea.barnatovich.auth.domain.usecase.GetUsersUseCase
import ru.mirea.barnatovich.auth.domain.usecase.IsLoggedInUseCase
import ru.mirea.barnatovich.auth.domain.usecase.LoginUseCase
import ru.mirea.barnatovich.auth.domain.usecase.LogoutUseCase

// Ручной DI
class AppContainer(context: Context) {

    private val tokenStorage = TokenStorage(context)
    private val api = DummyJsonApi(createHttpClient(tokenStorage))

    private val authRepository = AuthRepositoryImpl(api, tokenStorage)
    private val userRepository = UserRepositoryImpl(api)

    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val isLoggedInUseCase = IsLoggedInUseCase(authRepository)
    val getUsersUseCase = GetUsersUseCase(userRepository)
    val getUserUseCase = GetUserUseCase(userRepository)
}
