## 🛠 Технологический стек

### Backend
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)

### Базы данных и кэширование
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=for-the-badge&logo=databricks&logoColor=white)

### Frontend
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=websocket&logoColor=white)
![STOMP](https://img.shields.io/badge/STOMP-7D0B0B?style=for-the-badge)

### Инфраструктура
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![OAuth](https://img.shields.io/badge/OAuth2-EB5424?style=for-the-badge&logo=auth0&logoColor=white)
![Validation](https://img.shields.io/badge/Validation-3C6EB0?style=for-the-badge)

# 🌟 Social Network Platform

Красивое и современное веб-приложение социальной сети, разработанное с использованием передовых технологий Java экосистемы.

## ✨ Особенности проекта

- **🔐 Многоуровневая система аутентификации**: JWT-токены и OAuth2 через Google для безопасного входа
- **💌 Подтверждение регистрации**: Отправка ссылок подтверждения на электронную почту
- **💬 Реальное время**: Интерактивный чат и уведомления через WebSocket
- **🚀 Высокая производительность**: Кэширование с Redis для быстрого отклика
- **📱 Адаптивный интерфейс**: Удобный и современный UI с Thymeleaf
- **🧪 Гарантия качества**: Комплексные автотесты с JUnit и Mockito

## 🛠 Технологический стек

**Backend:**
- Java 17+
- Spring Boot (Security, Data JPA, Web, WebSocket)
- JWT аутентификация
- Google OAuth2 интеграция
- Java Mail для отправки писем
- Hibernate Validator

**Базы данных:**
- PostgreSQL - основное реляционное хранилище
- Redis - кэширование и управление сессиями

**Frontend:**
- Thymeleaf - шаблонизатор
- STOMP.js - работа с WebSocket
- JavaScript - интерактивность
- HTML5/CSS3 - адаптивная верстка

**Инфраструктура:**
- Docker - контейнеризация приложения
- Maven - управление зависимостями

## 🚀 Запуск проекта

1. Клонируйте репозиторий:
```bash
git clone https://github.com/your-username/social-network.git
```

2. Запустите через Docker Compose:
```bash
docker-compose up -d
```

3. Приложение будет доступно по адресу: `http://localhost:8080`

## 📦 Архитектура

Проект реализован с соблюдением принципов чистой архитектуры и разделения ответственности:
- Многослойная структура (контроллеры, сервисы, репозитории)
- DTO для передачи данных между слоями
- Глобальная обработка исключений
- Кастомные валидации данных

## 🧪 Тестирование

Проект включает комплекс автотестов:
- Unit-тесты сервисов с Mockito
- Интеграционные тесты REST API
- Тестирование безопасности и валидаций
- Покрытие ключевой бизнес-логики

## 🌐 Функциональность

- Регистрация и авторизация (JWT + OAuth2)
- Управление профилем пользователя
- Лента публикаций и новостей
- Система друзей и подписок
- Чат в реальном времени
- Уведомления о событиях
- Поиск пользователей

---

Разработано с ❤️ с использованием современных практик разработки. Приветствуются issues и pull requests!
