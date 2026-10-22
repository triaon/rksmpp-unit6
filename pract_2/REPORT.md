# Задание 2: Приложение «Нобелевские лауреаты»
Функционал приложения:

Создать приложение на Ktor Client + Clean Architecture, которое:
- показывает список лауреатов Нобелевской премии;
- позволяет фильтровать по году и/или категории;
- при клике на лауреата открывает экран с подробной информацией;
- API: https://api.nobelprize.org/2.1/nobelPrizes?limit=25&offset=0 (можно добавить параметры: nobelPrizeYear=2023, nobelPrizeCategory=physics и т.д.)

Требования к приложению:
1. Экран списка лауреатов (LazyColumn или LazyVerticalGrid):
   - Для каждого элемента:
     - Год премии;
     - Категория (physics, chemistry, literature, peace, medicine, economics);
     - Имя лауреата (fullName);
     - Краткое описание (motivation) — первые 100 символов.
   - Поля для фильтра: год (например, от 1901 до текущего) и категория (выпадающий список).
2. Экран детализации лауреатов:
   - Полное имя;
   - Год и категория;
   - Полное описание (motivation);
   - Страна (birthCountry / birthPlace);
   - Фото (если есть в ответе — поле portraitUrl или аналогичное).
3. Clean Architecture.

Обработка состояний: Loading (CircularProgressIndicator); Success (список); Error (текст ошибки + кнопка «Повторить»).

## Рисунки
- screenshots/01_spisok_laureatov.png | Список лауреатов Нобелевской премии (Ktor Client, LazyColumn)
- screenshots/02_filtr_kategoriya.png | Фильтр: ввод года и выпадающий список категорий
- screenshots/03_rezultat_filtra.png | Результат фильтрации: 1921 год, категория «Физика»
- screenshots/04_detali_laureata.png | Экран детализации лауреата: фото, полное имя, год и категория, страна, место рождения, полное описание
- screenshots/05_oshibka_net_seti.png | Состояние Error при отключённой сети с кнопкой «Повторить»
