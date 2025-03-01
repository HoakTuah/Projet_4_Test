# Yoga app

## Yoga app is a web application that allows users to book yoga classes. While teachers can create classes, users can book them.  

Technologies used : 
- Java 11
- Spring Boot 2.6.1
- Maven 3.8.1
- MySQL 8.0
- Angular 14.2.1
- Cypress 10.4.0
- Jest 28.1.3
- Jacoco 0.8.5
- JUnit 5.8.2
- Mockito 4.0.0

# Frontend / Backend Setup Instructions for testing

## Setup Steps 

### 1. Create the database
    - CREATE DATABASE test;

### 2. Create a User and flush privileges
    - CREATE USER 'your_username'@'localhost' IDENTIFIED BY 'your_password';
    - GRANT ALL PRIVILEGES ON test.* TO 'your_username'@'localhost';
    - FLUSH PRIVILEGES;

### 3. Launch SQL Script
     - Script can be found into the project here ressources/sql/script.sql


## Before running the tests, you need to run the Frontend and the Backend

### 1. To run the frontend :

    - navigate to the front folder => cd front
    - ng serve

### 2. To run the backend :

    - navigate to the back folder => cd back
    - mvn spring-boot:run 


### 3. To run unit and integration tests on the frontend :

    - navigate to the front folder => cd front 
    - npm run test
    - To check the coverage report for frontend go to the folder /front/coverage/jest/lcov-report/index.html

### 4. To run end to end tests and generate a report on the frontend :

    - navigate to the front folder => cd front
    - npm run e2e
    - execute the test "FullTest.cy.ts" with cypress
    - To check the coverage report for frontend go to the folder /front/coverage/lcov-report/index.html

### 5. To run the backend tests :

    - navigate to the back folder => cd back
    - mvn clean test
    - To check the coverage report for backend go to the folder /back/target/site/jacoco/index.html 
