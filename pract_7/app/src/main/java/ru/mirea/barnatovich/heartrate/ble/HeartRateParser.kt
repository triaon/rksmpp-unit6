package ru.mirea.barnatovich.heartrate.ble

// Разбор характеристики Heart Rate Measurement (0x2A37).
// Бит 0 первого байта (flags): 0 — пульс в UINT8, 1 — в UINT16 (little-endian).
object HeartRateParser {
    fun parse(data: ByteArray): Int? {
        if (data.size < 2) return null
        val flags = data[0].toInt() and 0xFF
        val isUint16 = flags and 0x01 != 0
        return if (isUint16) {
            if (data.size < 3) return null
            (data[1].toInt() and 0xFF) or ((data[2].toInt() and 0xFF) shl 8)
        } else {
            data[1].toInt() and 0xFF
        }
    }
}
