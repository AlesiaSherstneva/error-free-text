# error-free-text

Сервис для автоматической корректировки текста с использованием 
[API Яндекс Спеллер](https://yandex.ru/dev/speller/doc/ru/concepts/api-overview).

## Возможности

- Создание задач на проверку текста
- Проверка статуса обработки
- Автоматическая обработка задач по расписанию
- Поддержка русского и английского языков
- Автоопределение URL и цифр для настройки параметров проверки
- Разбиение длинных текстов на части (лимит API 10000 символов)

## Технологии

- Java 21
- Spring Boot 3.5.10
- PostgreSQL 15
- Gradle 8.5
- Docker, docker compose
- Liquibase

## Быстрый старт

### Запуск с Docker

```bash
# Клонировать репозиторий
git clone <repository-url>
cd error-free-text

# Собрать проект
./gradlew clean build

# Запустить
docker-compose up -d
```

### Запуск без Docker

```bash
# Требуется PostgreSQL (настройки в application.yaml)

# Собрать проект
./gradlew clean build

# Запустить
./gradlew bootRun
```

Приложение будет доступно по адресу `http://localhost:8080`

## API Endpoints

1. Создание задачи на корректировку
```bash
POST /tasks
Content-Type: application/json

{
    "text": "Я пешу очинь граматно, бес ашыпак!",
    "language": "RU"
}
```

Ответ:
```json
{
    "taskId": "550e8400-e29b-41d4-a716-446655440000"
}
```

2. Получение результата

```bash
GET /tasks/{taskId}
```

Ответ (задача ещё в обработке):
```json
{
    "status": "PENDING"
}
```

Ответ (задача выполнена):
```json
{
    "status": "COMPLETED",
    "correctedText": "Я пишу очень грамотно, без ошибок!"
}
```

Ответ (ошибка при выполнении задачи):
```json
{
    "status": "FAILED",
    "errorMessage": "API temporarily unavailable"
}
```

## Тестирование

```bash
# Запуск тестов (предварительно должен быть запущен Docker для работы Testcontainers)
./gradlew test

# Отчет о покрытии
./gradlew jacocoTestReport

# Отчет: build/reports/jacoco/html/index.html
```

- **Покрытие кода:** 97% (JaCoCo)
- **Юнит-тесты:** TaskService, YandexSpellerClient, YandexSpellerRequestConverter, TextUtils
- **Интеграционные тесты:** TaskController, TextCorrectionScheduler
- **Тестирование API:** WireMock
- **Тестирование работы с БД:** Testcontainers + PostgreSQL