# Yoga App

A web application that enables users to book yoga classes and allows teachers to create classes.

## Technologies Used

### Backend
- Java 11
- Spring Boot 2.6.1
- Maven 3.8.1
- MySQL 8.0
- JUnit 5.8.2
- Mockito 4.0.0
- Jacoco 0.8.5

### Frontend
- Angular 14.2.1
- Cypress 10.4.0
- Jest 28.1.3

## Setup Instructions

### 1. Clone the Project
```bash
git clone https://github.com/HoakTuah/Projet_4_Test.git
```

### 2. Database Setup
1. Create the database:
```sql
CREATE DATABASE test;
```

2. Create user and set privileges:
```sql
CREATE USER 'your_username'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON test.* TO 'your_username'@'localhost';
FLUSH PRIVILEGES;
```

3. Initialize database:
- Execute the SQL script located at `resources/sql/script.sql`

## Running the Application

### Frontend
```bash
cd front
ng serve
```

### Backend
```bash
cd back
mvn spring-boot:run
```

## Testing

### Frontend Tests
1. Unit and Integration Tests:
```bash
cd front
npm run test
```
- Coverage report: `/front/coverage/jest/lcov-report/index.html`

2. End-to-End Tests:
```bash
cd front
npm run e2e
```
- Execute `FullTest.cy.ts` with Cypress
- Coverage report: `/front/coverage/lcov-report/index.html`

### Backend Tests
```bash
cd back
mvn clean test
```
- Coverage report: `/back/target/site/jacoco/index.html`
