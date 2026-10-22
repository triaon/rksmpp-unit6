package ru.mirea.barnatovich.photocatalog

import android.app.Application

class PhotoApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
