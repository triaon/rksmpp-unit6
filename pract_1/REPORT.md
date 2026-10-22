# Задание 1: Приложение «Фотокаталог»
Функционал приложения:

Создать приложение на Retrofit + Clean Architecture, которое:
- показывает список случайных красивых фотографий;
- при клике на фото открывает экран детализации с большой картинкой;
- позволяет скачать фото в папку Downloads через SAF;
- API: https://picsum.photos/v2/list (открытый, без регистрации).

Требования к приложению:
1. Экран списка фотографий:
   - Grid или LazyVerticalGrid (по 2 столбца);
   - Для каждой фотографии: маленькая превьюшка (thumbnailUrl), автор (author), размеры (width × height);
   - При клике на фото — переход на экран детализации.
2. Экран детализации:
   - Большая картинка (download_url или url) через Coil/Glide;
   - Информация: автор, размеры, ссылка;
   - Кнопка «Скачать фото» — через SAF сохраняем в папку Downloads.
3. Clean Architecture.

Обработка состояний: Loading (CircularProgressIndicator); Success (список); Error (текст ошибки + кнопка «Повторить»).

## Рисунки
- screenshots/01_oshibka_net_seti.png | Состояние Error при отключённой сети с кнопкой «Повторить»
- screenshots/02_spisok_foto.png | Список фотографий picsum.photos в LazyVerticalGrid (2 столбца): превью, автор, размеры
- screenshots/03_detali_foto.png | Экран детализации: большая картинка (Coil), автор, размеры, ссылка, кнопка «Скачать фото»
- screenshots/04_saf_dialog_downloads.png | Системный диалог SAF (ACTION_CREATE_DOCUMENT) с выбранной папкой Downloads
- screenshots/05_foto_sohraneno.png | Сообщение об успешном сохранении фото
- screenshots/06_fajl_v_downloads.png | Сохранённый файл picsum_478.jpg в папке Downloads
