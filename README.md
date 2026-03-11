# Payment Service

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.9.0-red.svg)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![Keycloak](https://img.shields.io/badge/Keycloak-24.0.3-blueviolet.svg)](https://www.keycloak.org/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Manifests-326CE5.svg)](https://kubernetes.io/)
[![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-2088FF.svg)](.github/workflows/build.yml)

## 📌 О проекте

**Payment Service** — production-ready микросервис для обработки платежей, построенный на современном стеке с акцентом на безопасность, наблюдаемость и отказоустойчивость. Проект демонстрирует лучшие практики разработки распределенных систем.

---

## 🏗️ Архитектура и стек технологий

### Модули проекта

payment-service/

├── 📦 payment-service-app/ # Основной сервис платежей (Spring Boot 4)

├── 🔌 x-payment-adapter-app/ # Адаптер для внешних платежных систем

└── 📚 simple-http-server/ # Учебный модуль (исторический)


### Технологический стек

| Категория | Технологии |
|-----------|------------|
| **Язык** | Java 21 |
| **Фреймворк** | Spring Boot 4.0.0 |
| **База данных** | PostgreSQL 17, JPA (Hibernate), Liquibase |
| **Безопасность** | Spring Security, OAuth2 Resource Server (Keycloak 24.0.3) |
| **Асинхронность** | Apache Kafka, Spring Kafka |
| **Наблюдаемость** | Micrometer Tracing, Zipkin Brave, Actuator |
| **Маппинг** | MapStruct 1.6.3 |
| **Валидация** | Bean Validation |
| **Сборка** | Maven, Multi-module project |
| **Качество кода** | Checkstyle, Lombok |
| **CI/CD** | GitHub Actions |
| **Оркестрация** | Kubernetes (манифесты в `/K8s`) |

---

## 📡 API Эндпоинты

### Платежи (`/payments`)

| Метод | Эндпоинт | Роли | Описание |
|:-----:|----------|:----:|----------|
| `GET` | `/payments` | `USER`, `READER` | Получить все платежи |
| `GET` | `/{id}` | `USER` | Получить платеж по UUID |
| `POST` | `/` | `ADMIN` | Создать новый платеж |
| `PUT` | `/{id}` | `ADMIN` | Полное обновление платежа |
| `PATCH` | `/{id}/status` | `ADMIN` | Обновить только статус |
| `DELETE` | `/{id}` | `ADMIN` | Удалить платеж |
| `GET` | `/search` | `USER`, `READER`, `ADMIN` | Поиск с фильтрацией |

### Пример запроса (создание платежа)

```json
POST /payments
Authorization: Bearer <jwt-token>

{
  "amount": 1000.50,
  "currency": "RUB",
  "description": "Оплата заказа №123",
  "payerId": "user-456",
  "recipientId": "merchant-789"
}
```

### Пример ответа
```json
{
  "guid": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 1000.50,
  "currency": "RUB",
  "status": "PENDING",
  "createdAt": "2024-03-12T10:30:00Z",
  "updatedAt": "2024-03-12T10:30:00Z"
}
```

### Фильтрация платежей
```json GET /payments/search?status=COMPLETED&from=2024-01-01&to=2024-12-31&page=0&size=25&sort=createdAt,desc ```

Параметры фильтрации:

status — статус платежа (PENDING, COMPLETED, FAILED, CANCELLED)

from / to — диапазон дат (ISO format)

page / size — пагинация

sort — сортировка (например, createdAt,desc)


## 🔄 Асинхронное взаимодействие (Kafka)

### Топики

| Топик | Назначение |
|-------|------------|
| `xpayment-adapter.requests` | Запросы к внешнему платежному адаптеру |
| `xpayment-adapter.responses` | Ответы от адаптера |

### Конфигурация потребителя

| Параметр | Значение |
|----------|----------|
| **Group ID** | `xpayment-adapter-result-consumers` |
| **Auto offset reset** | `earliest` |
| **Commit mode** | Manual (ручное подтверждение) |
| **Concurrency** | 1 поток |

## 📊 Мониторинг и наблюдаемость

| Сервис | URL | Назначение |
|--------|-----|------------|
| **Zipkin** | `http://localhost:9411` | Распределенный трейсинг |
| **Actuator Metrics** | `http://localhost:8080/actuator/metrics` | Метрики приложения |
| **Health Check** | `http://localhost:8080/actuator/health` | Проверка состояния |
| **Keycloak Admin** | `http://localhost:8085` | Admin: `admin` / `admin` |

### 🔒 Безопасность (OAuth2)
Сервис интегрирован с Keycloak (Realm: pet-lms):

Все эндпоинты защищены JWT токенами

Роли: USER, READER, ADMIN

Issuer URI: http://localhost:8085/realms/pet-lms

### Пример получения токена

```json
curl -X POST http://localhost:8085/realms/pet-lms/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=payment-client" \
  -d "username=user" \
  -d "password=pass"
```

### Пример использования токена
```json
curl -X GET http://localhost:8080/payments \
  -H "Authorization: Bearer <your-jwt-token>"
```

### CI/CD Pipeline (GitHub Actions)
При создании Pull Request в ветку master автоматически запускается:

✅ Checkout кода

🔧 Установка JDK 21 (Temurin)

⚙️ Компиляция проекта (mvn compile)

🧪 Запуск тестов (mvn test)

🔍 Проверка Checkstyle для payment-service-app

---
📬 Контакты

Автор: Nikolay Tunkenov

GitHub: @TunkenovNikolay

