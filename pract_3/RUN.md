# Запуск: «Авторизация» (Ktor Client + DataStore + Clean Architecture)

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
