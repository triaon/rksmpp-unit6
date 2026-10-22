# Задание 3: Приложение «Авторизация»
Функционал приложения:

Создать приложение на Ktor Client (или Retrofit) + Clean Architecture, которое:
- позволяет пользователю авторизоваться (логин + пароль);
- после успешного входа показывает список пользователей;
- при клике на пользователя открывает экран с его детальной информацией;
- сохраняет токен авторизации в DataStore;
- обрабатывает выход из аккаунта (удаление токена);
- API: https://dummyjson.com/ (открытый тестовый API).

Обязательные эндпоинты:
- POST /auth/login — авторизация (возвращает token и данные пользователя);
- GET /users — список всех пользователей (требует Bearer Token);
- GET /users/{id} — детальная информация о пользователе (требует Bearer Token).

Тестовые учётные данные:
- username: emilys
- password: emilyspass

Требования к приложению:
1. Экран авторизации (LoginScreen):
   - поля для username и password;
   - кнопка «Войти»;
   - индикатор загрузки;
   - обработка ошибок («Неверные данные», «Нет соединения» и т.д.);
   - после успеха — переход на список пользователей.
2. Экран списка пользователей (UsersListScreen):
   - LazyColumn / LazyVerticalGrid;
   - для каждого пользователя: аватар (image), имя (firstName + lastName), username, email;
   - при клике — переход на детали.
3. Экран детальной информации о пользователе (UserDetailScreen):
   - большая аватарка;
   - имя, фамилия и email;
   - кнопка «Выйти» (очистка токена + возврат на логин).
4. Clean Architecture.
5. Хранение токена:
   - использовать DataStore (Preferences DataStore);
   - сохранять токен после успешного логина;
   - добавлять Authorization: Bearer $token ко всем запросам после авторизации;
   - при выходе — очищать токен.
6. Обязательная обработка состояний:
   - Loading (CircularProgressIndicator);
   - Success (данные);
   - Error (текст ошибки + кнопка «Повторить»).

## Рисунки
- screenshots/01_ekran_vhoda.png | Экран авторизации с введёнными тестовыми данными emilys / emilyspass
- screenshots/02_oshibka_nevernye_dannye.png | Ошибка «Неверные данные» при неправильном пароле
- screenshots/03_oshibka_net_soedineniya.png | Ошибка «Нет соединения» при отключённой сети
- screenshots/04_spisok_polzovatelej.png | Список пользователей после входа: аватар, имя и фамилия, username, email
- screenshots/05_detali_polzovatelya.png | Детальная информация о пользователе: большая аватарка, имя, фамилия, email, кнопка «Выйти»
- screenshots/06_spisok_oshibka_povtorit.png | Запуск с сохранённым токеном без сети: сразу экран списка, ошибка и кнопка «Повторить»
- screenshots/07_posle_vyhoda.png | Экран входа после нажатия «Выйти» (токен удалён из DataStore)
- screenshots/08_http_log_bearer.txt | Лог HTTP-запросов Ktor: после входа к GET /users и GET /users/{id} добавляется заголовок Authorization (значение скрыто)
