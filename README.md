# Сервис авторизации пользователей

Простой и безопасный сервис авторизации на Spring Boot, реализующий token-based подход. Позволяет регистрироваться, входить, обновлять токен и выходить из системы.

## 🛠 Технологии

- **Java 17**
- **Spring Boot 3.1.5**
- **PostgreSQL** — для хранения пользователей и токенов
- **Flyway** — для миграций БД
- **UUID-токены** — для сессий с ограниченным временем жизни
- **BCrypt** — для шифрования паролей
- **Spring Data JPA** — для работы с БД
- **SpringDoc OpenAPI (Swagger UI)** — для документации API

Файл: src/main/resources/application-db.properties
    ```bash
    ### Укажите URL вашей базы данных (например, локальная или удалённая)
    spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
    
    - Имя пользователя БД (например, postgres)
    spring.datasource.username=postgres
    
    - Пароль пользователя БД (например, postgres)
    spring.datasource.password=postgres
    
    - Драйвер БД (менять не нужно)
    spring.datasource.driver-class-name=org.postgresql.Driver

После запуска откройте в браузере:

http://localhost:8080/swagger-ui.html

### 📡 Доступные эндпоинты

| Метод | Эндпоинт | Описание |
|------|---------|--------|
| POST | [POST /auth/register](http://localhost:8080/auth/register) | Регистрация нового пользователя |
| POST | [POST /auth/login](http://localhost:8080/auth/login) | Вход (возвращает токен) |
| POST | [POST /auth/refresh-token](http://localhost:8080/auth/refresh-token) | Обновление токена (по старому) |
| POST | [POST /auth/logout](http://localhost:8080/auth/logout) | Выход (отзыв токена) |