# ZooManagementWeb — Система управления зоопарком

Веб-приложение на **Spring Boot 3 + Thymeleaf + MySQL**  
Курсовая работа → превратилась в настоящий продукт уровня junior+

## Функции
- Авторизация с ролями (ADMIN, CASHIER, KEEPER, VET)
- Управление животными + загрузка фото
- Онлайн-продажа билетов (4 типа) с расчётом суммы
- Расписание и учёт кормлений
- Статистика по кормам и билетам
- Красивый адаптивный интерфейс на Bootstrap 5

## Роли и права
| Роль      | Доступ                                                                 |
|-----------|------------------------------------------------------------------------|
| ADMIN     | Всё                                                                    |
| CASHIER   | Главная → Животные → Билеты (продажа + статистика)                    |
| KEEPER    | Главная → Животные → Кормления (расписание + статистика)              |
| VET       | Главная → Животные                                                     |

## Скриншоты
## Скриншоты

![Главная страница](screenshots/home.jpg)
![Продажа билетов](screenshots/tickets.jpg)
![Статистика продажи билетов](screenshots/tickets_statistic.jpg)

## Запуск

# 1. Создать БД zoo_db в MySQL
# 2. application.properties — указать свой пароль от MySQL
# 3. Запустить ZooManagementWebApplication.java
# 4. Открыть http://localhost:8080

## Логин: admin / cashier / vet / keeper → пароль: 123

## Стек
Spring Boot 3, Spring Security, Spring Data JPA
Thymeleaf + Bootstrap 5
MySQL
Lombok

## Автор: Железнякова Людмила