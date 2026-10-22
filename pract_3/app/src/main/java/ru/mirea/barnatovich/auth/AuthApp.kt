package ru.mirea.barnatovich.auth

import android.app.Application

class AuthApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
