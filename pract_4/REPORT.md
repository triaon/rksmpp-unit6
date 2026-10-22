# Задание 4: Серверное приложение “Nobel Prize API”
Создать сервер API на фреймворке Ktor, который будет имитировать часть функционала Nobel Prize API + иметь авторизацию.

Требования:
1. Эндпоинты:
   - POST /auth/login - логин/пароль - JWT-токен;
   - GET /prizes - список всех премий (защищённый);
   - GET /prizes/{year}/{category} - детальная премия (защищённый);
   - GET /prizes/{year}/{category}/laureates - список лауреатов премии (защищённый)
2. Авторизация: JWT, срок 30 минут, ключ подписи — любой ≥32 символа;
3. Данные: в памяти (List<NobelPrize>) или в файлах;
4. Логирование запросов в консоль – CallLogging;
5. Clean Architecture;

Все данные для заполнения структур взяты из публичного API https://api.nobelprize.org/2.1/nobelPrizes (60 премий за 2015–2024 годы, 123 лауреата), один раз сохранены в JSON-ресурс и загружаются в память при старте сервера.

## Рисунки
- screenshots/01_login.txt | Авторизация POST /auth/login: получение JWT-токена (срок действия 1800 секунд)
- screenshots/02_login_wrong.txt | Авторизация с неверным паролем: ответ 401
- screenshots/03_prizes_no_token.txt | Запрос GET /prizes без токена и с недействительным токеном: ответ 401
- screenshots/04_prizes.txt | GET /prizes с токеном: список премий (начало ответа)
- screenshots/05_prize_detail.txt | GET /prizes/{year}/{category}: детальная информация о премии и ответ 404 для несуществующей премии
- screenshots/06_laureates.txt | GET /prizes/{year}/{category}/laureates: список лауреатов премии с токеном и без него
- screenshots/07_server_log.txt | Лог сервера: запросы, записанные плагином CallLogging
