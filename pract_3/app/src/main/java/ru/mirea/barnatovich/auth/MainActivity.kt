package ru.mirea.barnatovich.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import ru.mirea.barnatovich.auth.presentation.AuthNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as AuthApp).container
        setContent {
            MaterialTheme {
                AuthNavHost(container)
            }
        }
    }
}
