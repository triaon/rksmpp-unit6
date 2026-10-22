package ru.mirea.barnatovich.heartrate.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.mirea.barnatovich.heartrate.ble.ConnectionState

private val blePermissions: Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

@Composable
fun HeartRateScreen(viewModel: HeartRateViewModel = viewModel()) {
    val context = LocalContext.current
    val devices by viewModel.devices.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val message by viewModel.message.collectAsState()

    var permissionsGranted by remember {
        mutableStateOf(blePermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionsGranted = result.values.all { it }
    }

    // как только разрешения есть — сразу начинаем поиск
    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) viewModel.startScan() else permissionLauncher.launch(blePermissions)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Монитор сердечного ритма", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Heart Rate: " + (heartRate?.let { "$it bpm" } ?: "—"),
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("Статус: ${connectionState.title}", style = MaterialTheme.typography.titleMedium)
        deviceInfo?.let { Text("Устройство: $it", style = MaterialTheme.typography.bodyMedium) }
        message?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            if (!permissionsGranted) permissionLauncher.launch(blePermissions)
            else if (isScanning) viewModel.stopScan()
            else viewModel.startScan()
        }) {
            Text(
                when {
                    !permissionsGranted -> "Выдать разрешения"
                    isScanning -> "Остановить сканирование"
                    else -> "Начать сканирование"
                }
            )
        }

        if (connectionState == ConnectionState.CONNECTED) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { viewModel.refresh() }) { Text("Обновить данные") }
                OutlinedButton(onClick = { viewModel.disconnect() }) { Text("Отключиться") }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Найденные устройства", style = MaterialTheme.typography.titleMedium)
            if (isScanning) {
                Spacer(Modifier.width(8.dp))
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            }
        }
        Spacer(Modifier.height(8.dp))
        if (devices.isEmpty() && !isScanning) {
            Text("Устройства не найдены", style = MaterialTheme.typography.bodyLarge)
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(devices, key = { it.address }) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.connect(device) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(device.name, style = MaterialTheme.typography.titleMedium)
                        Text("${device.address}  •  RSSI ${device.rssi} dBm", style = MaterialTheme.typography.bodySmall)
                        if (device.hasHeartRate) {
                            Text("Heart Rate Service", color = Color(0xFFE53935), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
