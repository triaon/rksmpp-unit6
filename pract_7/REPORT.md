# Задание 7: BLE приложение «Монитор сердечного ритма»
Модифицировать представленный пример:

1. Приложение подключается к устройствам с Heart Rate Service (UUID 0x180D);
2. Читает и отображает в реальном времени частоту сердечных сокращений (Heart Rate Measurement, UUID 0x2A37) через уведомления.

Обязательные экраны и функции:

1. Главный экран (сканер) — такой же, как в примере: кнопка сканирования, статус подключения, список найденных устройств, кнопки «Обновить данные» и «Отключиться» после подключения.
2. Вместо давления показывается текущая частота пульса: «Heart Rate: 72 bpm» (или «—»).

Рекомендуемые UUID (стандартные):

- Service: 0000180d-0000-1000-8000-00805f9b34fb (Heart Rate Service);
- Characteristic: 00002a37-0000-1000-8000-00805f9b34fb (Heart Rate Measurement);
- Descriptor: 00002902-0000-1000-8000-00805f9b34fb (Client Characteristic Configuration).

Подписка на уведомления включается записью ENABLE_NOTIFICATION_VALUE в CCC-дескриптор 0x2902. Значение пульса разбирается по флагу в первом байте: бит 0 = 0 — UINT8, бит 0 = 1 — UINT16. Приложение запрашивает разрешения BLUETOOTH_SCAN, BLUETOOTH_CONNECT и ACCESS_FINE_LOCATION.

Реального пульсометра не было, поэтому для проверки к эмулятору через его gRPC API подключён виртуальный BLE-датчик (скрипт `tools/virtual_hr_sensor.py`). Он объявляет Heart Rate Service и раз в секунду отправляет уведомления 0x2A37 в форматах UINT8 и UINT16.

## Рисунки
- screenshots/01_permissions.png | Запрос разрешения на поиск устройств поблизости (BLUETOOTH_SCAN/CONNECT)
- screenshots/02_scan.png | Главный экран сканера со списком найденных BLE-устройств
- screenshots/03_connected.png | Подключение к датчику: пульс приходит через уведомления 0x2A37
- screenshots/04_logcat.txt | Лог приложения: подключение, запись CCC-дескриптора и уведомления о пульсе
- screenshots/05_disconnected.png | После отключения: «Heart Rate: —», статус «Отключено»

## Листинги
- app/src/main/java/ru/mirea/barnatovich/heartrate/ui/HeartRateScreen.kt | Экран сканера и пульса (Compose)
- app/src/main/java/ru/mirea/barnatovich/heartrate/ble/HeartRateBleManager.kt | BLE-менеджер: сканирование, GATT, подписка на уведомления
- app/src/main/java/ru/mirea/barnatovich/heartrate/ble/HeartRateParser.kt | Разбор формата Heart Rate Measurement (UINT8/UINT16)
