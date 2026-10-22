# Запуск: «Нобелевские лауреаты» (Ktor Client + Clean Architecture)

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
