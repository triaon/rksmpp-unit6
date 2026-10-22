package ru.mirea.barnatovich.nobelclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import ru.mirea.barnatovich.nobelclient.presentation.NobelNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as NobelApp).container
        setContent {
            MaterialTheme {
                NobelNavHost(container)
            }
        }
    }
}
