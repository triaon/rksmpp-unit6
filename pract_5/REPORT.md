# Задание 5: Серверное приложение “Nobel Prize API” c хранением данных в PostgreSQL
Доработать сервер API из задания 4, который будет использовать для хранения данных СУБД PostgreSQL.

Вместо neon.tech использован локальный PostgreSQL в Docker; для neon достаточно заменить DATABASE_URL.

Требования:
- Авторизует пользователей;
- Получает данные из публичного Nobel Prize API (допускается разовый ручной перенос);
- Сохраняет их в PostgreSQL;
- Позволяет пользователям добавлять/удалять любимые премии;
- Логирование запросов в консоль – CallLogging;
- Clean Architecture;
- Добавить описание сервиса с помощью SwaggerUI и/или Redocly: https://smiley4.github.io/ktor-openapi-tools/latest/

Все данные для заполнения структур взяты из https://api.nobelprize.org/2.1 (премии за 2015–2024 годы): при первом запуске, если таблица prizes пуста, сервер сам импортирует их в БД.

Таблицы:
1. users — id, username, password_hash (bcrypt), role;
2. prizes — id, award_year, category, full_name, motivation, detail_link (+ date_awarded, prize_amount);
3. laureates — id, prize_id, full_name, portion, motivation, portrait_url (+ nobel_id, birth_country, birth_place);
4. user_prizes — user_id, prize_id, added_at.

Эндпоинты:

Открытые:
- POST /login — авторизация;
- GET /prizes — список премий (кэш в БД, данные загружены из API), фильтры ?year= и ?category=;

Защищённые:
- GET /users/me — профиль;
- GET /users/me/prizes — избранные премии;
- POST /users/me/prizes/{prizeId} — добавить премию;
- DELETE /users/me/prizes/{prizeId} — удалить премию.

## Рисунки
- screenshots/01_login.txt | Авторизация POST /login: успешный вход (JWT-токен) и неверный пароль
- screenshots/02_prizes.txt | Открытый запрос GET /prizes: список премий из PostgreSQL и фильтр по году и категории
- screenshots/03_no_token_401.txt | Обращение к защищённым эндпоинтам без токена: ответ 401
- screenshots/04_users_me.txt | GET /users/me: профиль пользователя
- screenshots/05_add_favorite.txt | POST /users/me/prizes/{prizeId}: добавление премии в избранное, повторное добавление (409) и несуществующая премия (404)
- screenshots/06_favorites.txt | GET /users/me/prizes: список избранных премий
- screenshots/07_delete_favorite.txt | DELETE /users/me/prizes/{prizeId}: удаление премии из избранного
- screenshots/08_server_log.txt | Лог сервера: импорт данных из Nobel Prize API и запросы, записанные CallLogging
- screenshots/09_swagger_ui.png | Описание сервиса в Swagger UI
- screenshots/10_swagger_get_prizes.png | Swagger UI: документация эндпоинта GET /prizes
- screenshots/11_swagger_add_favorite.png | Swagger UI: документация защищённого эндпоинта POST /users/me/prizes/{prizeId}
- screenshots/12_psql_tables.txt | Таблицы и данные в PostgreSQL (psql)
