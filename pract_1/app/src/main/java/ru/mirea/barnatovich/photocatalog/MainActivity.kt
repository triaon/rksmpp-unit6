package ru.mirea.barnatovich.photocatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import ru.mirea.barnatovich.photocatalog.presentation.PhotoNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as PhotoApp).container
        setContent {
            MaterialTheme {
                PhotoNavHost(container)
            }
        }
    }
}
