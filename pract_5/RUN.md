# Запуск сервера (задание 5)

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
