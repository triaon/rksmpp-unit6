# РКСМП, Модуль 6 – Сетевое взаимодействие

Барнатович И.В., ИКБО-65-23. Каждая папка `pract_*` — отдельный проект.

Перед запуском:

- Android-проекты открывать в Android Studio: **File → Open → папка практики** (например, `pract_3`),
  дождаться синхронизации Gradle и нажать **Run** на эмуляторе (Pixel, Android 14+).
- Из терминала: в папке практики `./gradlew assembleDebug`, затем `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
- Нужен JDK 17+ (подходит встроенный в Android Studio):
  `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`.

## `pract_1` — «Фотокаталог» (Retrofit + Clean Architecture)

Код: https://github.com/triaon/rksmpp-unit6/tree/main/pract_1

Открыть папку `pract_1` в Android Studio и нажать Run, или из терминала:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n ru.mirea.barnatovich.photocatalog/.MainActivity
```

Что показать:
1. Сетка 2 столбца со случайной страницей каталога picsum (превью, автор, ширина × высота).
2. Нажать на фото — детали: большая картинка, автор, размеры, ссылка на Unsplash.
3. «Скачать фото» — откроется системный диалог сохранения (SAF), выбрать Downloads и нажать «Сохранить».
   Появится «Фото сохранено», файл виден в приложении «Файлы» → Downloads.
4. Выключить сеть (`adb shell svc wifi disable; adb shell svc data disable`) и перезапустить приложение — ошибка и кнопка «Повторить».

## `pract_2` — «Нобелевские лауреаты» (Ktor Client + Clean Architecture)

Код: https://github.com/triaon/rksmpp-unit6/tree/main/pract_2

Открыть папку `pract_2` в Android Studio и нажать Run, или из терминала:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n ru.mirea.barnatovich.nobel/.MainActivity
```

Что показать:
1. При запуске — список последних премий (год, категория, имя, первые 100 символов motivation).
2. Ввести год (например, 1921), выбрать категорию «Физика», нажать «Применить» — Albert Einstein. «Сбросить» — снять фильтр.
3. Нажать на лауреата — детали: фото (из Википедии по ссылке из ответа API), год и категория, страна, место рождения, полный motivation.
4. Выключить сеть (`adb shell svc wifi disable; adb shell svc data disable`), нажать «Сбросить» — ошибка и кнопка «Повторить»; включить сеть и нажать «Повторить».

Адрес API задаётся в `AppContainer.BASE_URL`, запросы — в `data/remote/NobelApi.kt`.

## `pract_3` — «Авторизация» (Ktor Client + DataStore + Clean Architecture)

Код: https://github.com/triaon/rksmpp-unit6/tree/main/pract_3

Открыть папку `pract_3` в Android Studio и нажать Run, или из терминала:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n ru.mirea.barnatovich.auth/.MainActivity
```

Что показать:
1. Ввести `emilys` и неверный пароль, нажать «Войти» — «Неверные данные». Без сети — «Нет соединения».
2. Ввести `emilys` / `emilyspass` — индикатор на кнопке, затем список пользователей.
3. Нажать на пользователя — профиль (аватар, имя, фамилия, email).
4. Закрыть и снова открыть приложение — сразу откроется список (токен лежит в DataStore).
5. В профиле нажать «Выйти» — возврат на вход; после перезапуска снова экран входа.
6. Заголовок Bearer в запросах: `adb logcat -s HTTP` (значение токена скрыто).

## `pract_4` — сервера (задание 4)

Код: https://github.com/triaon/rksmpp-unit6/tree/main/pract_4

Нужен JDK 21. Сервер слушает порт 8080.

```bash
./gradlew run
```

Проверка (в другом терминале): получить токен и запросить защищённые эндпоинты.

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" \
  -d '{"username":"student","password":"student123"}' | jq -r .token)
curl -i http://localhost:8080/prizes                                   # 401 без токена
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/prizes/2023/physics
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/prizes/2023/physics/laureates
```

Пользователи: `student / student123`, `admin / admin123`. Ключ JWT можно задать переменной `JWT_SECRET` (не короче 32 символов),
порт — переменной `PORT`. Запросы видны в консоли сервера (CallLogging).

## `pract_5` — сервера (задание 5)

Код: https://github.com/triaon/rksmpp-unit6/tree/main/pract_5

Нужны JDK 21 и Docker. Вместо neon.tech используется локальный PostgreSQL в Docker.

```bash
docker compose up -d          # контейнер rksmpp-pg, порт 127.0.0.1:55432
./gradlew run                 # сервер на http://localhost:8080
```

При первом запуске сервер сам загружает премии 2015–2024 из api.nobelprize.org (около 20 секунд, в логе
«Импортировано премий...»). Документация: http://localhost:8080/swagger

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/login -H "Content-Type: application/json" \
  -d '{"username":"student","password":"student123"}' | jq -r .token)
curl "http://localhost:8080/prizes?year=2023&category=physics"
curl -X POST -H "Authorization: Bearer $TOKEN" http://localhost:8080/users/me/prizes/8
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/users/me/prizes
```

Подключение задаётся переменными `DATABASE_URL`, `DB_USER`, `DB_PASSWORD`. Для neon.tech:
`DATABASE_URL="jdbc:postgresql://<host>.neon.tech/neondb?sslmode=require"`. Остановить БД: `docker compose stop`.

## `pract_6` — клиент для собственного сервера (задание 6)

Код: https://github.com/triaon/rksmpp-unit6/tree/main/pract_6

Сначала запустить сервер из задания 5 (в папке `pract_5`):

```bash
docker compose up -d && ./gradlew run
```

Затем открыть `pract_6` в Android Studio и нажать Run на эмуляторе, или из терминала:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n ru.mirea.barnatovich.nobelclient/.MainActivity
```

Что показать:
1. Вход: `student` / `student123` (неверный пароль — сообщение «Неверный логин или пароль»).
2. Список лауреатов с сервера; год 2023 + «Физика» → «Применить» (в БД премии 2015–2024 годов).
3. Нажать на лауреата — детали и фото; кнопка «Добавить премию в избранное»; сердечко вверху — список избранного.
4. Остановить сервер и нажать «Применить» — ошибка «Сервер недоступен» и кнопка «Повторить».
5. Запросы приложения видны в консоли сервера. Адрес сервера — `AppContainer.BASE_URL`.

## `pract_7` — «Монитор сердечного ритма» (BLE, Heart Rate Service)

Код: https://github.com/triaon/rksmpp-unit6/tree/main/pract_7

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

В приложении нажать «Начать сканирование» → «HR Sensor (virtual)» → пульс обновляется раз в секунду.
