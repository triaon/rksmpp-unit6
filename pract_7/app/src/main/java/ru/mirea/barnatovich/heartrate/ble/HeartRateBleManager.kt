package ru.mirea.barnatovich.heartrate.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class FoundDevice(
    val device: BluetoothDevice,
    val name: String,
    val address: String,
    val rssi: Int,
    val hasHeartRate: Boolean
)

enum class ConnectionState(val title: String) {
    DISCONNECTED("Отключено"),
    CONNECTING("Подключение…"),
    CONNECTED("Подключено")
}

// Все вызовы BLE делаются только после выдачи разрешений на экране
@SuppressLint("MissingPermission")
class HeartRateBleManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private val _devices = MutableStateFlow<List<FoundDevice>>(emptyList())
    val devices: StateFlow<List<FoundDevice>> = _devices

    private val _heartRate = MutableStateFlow<Int?>(null)
    val heartRate: StateFlow<Int?> = _heartRate

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    // имя и адрес устройства, к которому подключаемся
    private val _deviceInfo = MutableStateFlow<String?>(null)
    val deviceInfo: StateFlow<String?> = _deviceInfo

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private var gatt: BluetoothGatt? = null
    private var scanTimeout: Job? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val name = result.scanRecord?.deviceName ?: result.device.name
            if (name.isNullOrBlank()) return
            val hasHr = result.scanRecord?.serviceUuids?.contains(ParcelUuid(HEART_RATE_SERVICE)) == true
            val found = FoundDevice(result.device, name, result.device.address, result.rssi, hasHr)
            _devices.update { list ->
                // устройства с Heart Rate Service показываем первыми
                (list.filterNot { it.address == found.address } + found)
                    .sortedWith(compareByDescending<FoundDevice> { it.hasHeartRate }.thenByDescending { it.rssi })
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.w(TAG, "Сканирование не удалось: $errorCode")
            _isScanning.value = false
            _message.value = "Ошибка сканирования ($errorCode)"
        }
    }

    fun startScan() {
        if (adapter == null || !adapter.isEnabled) {
            _message.value = "Bluetooth выключен"
            return
        }
        val scanner = adapter.bluetoothLeScanner ?: return
        if (_isScanning.value) scanner.stopScan(scanCallback)
        _devices.value = emptyList()
        _message.value = null
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        scanner.startScan(null, settings, scanCallback)
        _isScanning.value = true
        Log.d(TAG, "Сканирование запущено")

        // через 15 секунд останавливаем поиск, чтобы не тратить батарею
        scanTimeout?.cancel()
        scanTimeout = scope.launch {
            delay(SCAN_PERIOD_MS)
            stopScan()
        }
    }

    fun stopScan() {
        scanTimeout?.cancel()
        if (_isScanning.value) {
            adapter?.bluetoothLeScanner?.stopScan(scanCallback)
            _isScanning.value = false
            Log.d(TAG, "Сканирование остановлено")
        }
    }

    fun connect(device: FoundDevice) {
        stopScan()
        gatt?.close()
        _heartRate.value = null
        _connectionState.value = ConnectionState.CONNECTING
        _deviceInfo.value = "${device.name} (${device.address})"
        gatt = device.device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        Log.d(TAG, "Подключение к ${device.address}")
    }

    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connectionState.value = ConnectionState.DISCONNECTED
        _deviceInfo.value = null
        _heartRate.value = null
    }

    // ручное чтение характеристики (если устройство разрешает read)
    fun refresh() {
        val g = gatt ?: return
        val characteristic = g.getService(HEART_RATE_SERVICE)?.getCharacteristic(HEART_RATE_MEASUREMENT) ?: return
        g.readCharacteristic(characteristic)
    }

    private fun enableNotifications(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        g.setCharacteristicNotification(characteristic, true)
        val ccc = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG) ?: return
        val value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            g.writeDescriptor(ccc, value)
        } else {
            @Suppress("DEPRECATION")
            ccc.value = value
            @Suppress("DEPRECATION")
            g.writeDescriptor(ccc)
        }
    }

    private fun onHeartRateBytes(data: ByteArray) {
        val bpm = HeartRateParser.parse(data) ?: return
        _heartRate.value = bpm
        Log.d(TAG, "Пульс: $bpm bpm")
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange status=$status newState=$newState")
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = ConnectionState.CONNECTED
                    g.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = ConnectionState.DISCONNECTED
                    _deviceInfo.value = null
                    _heartRate.value = null
                    g.close()
                    if (gatt == g) gatt = null
                }
            }
        }

        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                _message.value = "Не удалось получить сервисы устройства"
                return
            }
            val characteristic = g.getService(HEART_RATE_SERVICE)?.getCharacteristic(HEART_RATE_MEASUREMENT)
            if (characteristic == null) {
                _message.value = "У устройства нет Heart Rate Service"
                return
            }
            enableNotifications(g, characteristic)
        }

        override fun onDescriptorWrite(g: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            Log.d(TAG, "CCC записан, status=$status — уведомления включены")
        }

        // Android 13+
        override fun onCharacteristicChanged(
            g: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic.uuid == HEART_RATE_MEASUREMENT) onHeartRateBytes(value)
        }

        // Android 12 и ниже
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic.uuid == HEART_RATE_MEASUREMENT) {
                @Suppress("DEPRECATION")
                characteristic.value?.let { onHeartRateBytes(it) }
            }
        }

        // Android 13+
        override fun onCharacteristicRead(
            g: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) onHeartRateBytes(value)
        }

        // Android 12 и ниже
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                @Suppress("DEPRECATION")
                characteristic.value?.let { onHeartRateBytes(it) }
            }
        }
    }

    companion object {
        private const val TAG = "HeartRateBle"
        private const val SCAN_PERIOD_MS = 15_000L

        val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        val HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        val CLIENT_CHARACTERISTIC_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
