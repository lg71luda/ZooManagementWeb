# ZooManagementWeb — Zoo Management System


Web application on **Spring Boot 3 + Thymeleaf + MySQL**  
Coursework has → turned into a real product of the junior+ level


## Features
- Authorization with roles (ADMIN, CASHIER, KEEPER, VET)
- Animal management + photo uploading
- Online ticket sales (4 types) with calculation of the amount
- Feeding schedule and accounting
- Feed and ticket statistics
- Beautiful responsive interface on Bootstrap 5


## Roles and rights
| Role | Access |
|-----------|------------------------------------------------------------------------|
| ADMIN     | All |
| CASHIER   | Home → Animals → Tickets (Sale + Statistics) |
| KEEPER    | Home → Animals → Feeding (Schedule + Statistics) |
| VET       | Home → Animals |


## Launch
```bash
# 1. Create a zoo_db database in MySQL
# 2. application.properties — enter your MySQL password
# 3. Launch ZooManagementWebApplication.java
# 4. Open http://localhost:8080


## Stack


Spring Boot 3, Spring Security, Spring Data JPA
Thymeleaf + Bootstrap 5
MySQL
Lombok


Author: Железнякова Людмила
