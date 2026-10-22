# Запуск: «Монитор сердечного ритма» (BLE, Heart Rate Service)

Открыть папку `pract_7` в Android Studio и нажать Run, или из терминала:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n ru.mirea.barnatovich.heartrate/.MainActivity
```

На телефоне с пульсометром (нагрудный датчик, часы с HR-трансляцией): разрешить «Устройства поблизости»
и геолокацию → сканирование стартует само → нажать на датчик → появится «Heart Rate: NN bpm».

Без пульсометра — виртуальный датчик в эмуляторе (эмулятор запускается с `-grpc 8658`):

```bash
emulator -avd <имя_avd> -grpc 8658 &
python3 -m venv .venv && .venv/bin/pip install grpcio grpcio-tools
L=~/Library/Android/sdk/emulator/lib; mkdir -p tools/gen
.venv/bin/python -m grpc_tools.protoc -I $L --python_out=tools/gen --grpc_python_out=tools/gen \
  $L/emulated_bluetooth.proto $L/emulated_bluetooth_device.proto $L/grpc_endpoint_description.proto
.venv/bin/python tools/virtual_hr_sensor.py
```

В приложении нажать «Начать сканирование» → «Пульсометр (виртуальный)» → пульс обновляется раз в секунду.
