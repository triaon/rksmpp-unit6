package ru.mirea.barnatovich.heartrate.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.mirea.barnatovich.heartrate.ble.FoundDevice
import ru.mirea.barnatovich.heartrate.ble.HeartRateBleManager

class HeartRateViewModel(application: Application) : AndroidViewModel(application) {

    private val ble = HeartRateBleManager(application)

    val devices = ble.devices
    val heartRate = ble.heartRate
    val connectionState = ble.connectionState
    val deviceInfo = ble.deviceInfo
    val isScanning = ble.isScanning
    val message = ble.message

    fun startScan() = ble.startScan()
    fun stopScan() = ble.stopScan()
    fun connect(device: FoundDevice) = ble.connect(device)
    fun refresh() = ble.refresh()
    fun disconnect() = ble.disconnect()

    override fun onCleared() {
        ble.stopScan()
        ble.disconnect()
    }
}
