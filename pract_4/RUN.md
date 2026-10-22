# Запуск сервера (задание 4)

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
