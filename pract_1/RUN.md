# Запуск: «Фотокаталог» (Retrofit + Clean Architecture)

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
