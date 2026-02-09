<p style="text-align: center;">
  <img src="front/src/assets/images/logo.svg" alt="MDD Logo" width="200"/>
</p>

<h1 style="text-align: center;">MDD (Monde de Dev)</h1>

<p style="text-align: center;">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen?logo=springboot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Angular-20.3-red?logo=angular" alt="Angular"/>
  <img src="https://img.shields.io/badge/MySQL-8.x-blue?logo=mysql" alt="MySQL"/>
</p>

---

## About The Project

MDD (Monde de Dev) is a social network MVP designed for developers to connect with peers who share common interests in programming topics. Built as part of the OpenClassroom program (Project 6), it provides a platform where developers can subscribe to topics, share articles, and engage through comments.

### Key Features

- **User Authentication** - Registration with email, password, and username; login via email OR username
- **Programming Topics** - Browse and discover various programming themes
- **Topic Subscriptions** - Subscribe to topics of interest from the topics page
- **Personalized Feed** - Chronological article feed based on subscribed topics with reversible sorting
- **Article Creation** - Create articles linked to specific topics with title and content
- **Comments** - Non-recursive comments on articles with automatic author and date attribution
- **User Profile** - Editable profile with subscription management (unsubscribe from profile only)

---

## Tech Stack

### Backend
| Technology | Description |
|------------|-------------|
| Java 21 | Programming language |
| Spring Boot 3.5.8 | Application framework |
| Spring Security | Authentication & authorization |
| JWT (jjwt 0.12.6) | Token-based authentication |
| Spring Data JPA | Database access |
| MapStruct 1.6.3 | Object mapping |
| Lombok | Boilerplate reduction |
| Springdoc OpenAPI 2.7.0 | API documentation |

### Frontend
| Technology | Description |
|------------|-------------|
| Angular 20.3 | Frontend framework |
| TypeScript 5.9 | Programming language |
| RxJS 7.8 | Reactive extensions |
| Angular Material 20.2 | UI component library |

### Database & Testing
| Technology | Description |
|------------|-------------|
| MySQL 8.x | Relational database |
| JUnit 5 | Backend unit testing |
| Mockito | Backend mocking framework |
| JaCoCo | Code coverage (70% line, 60% branch minimum) |
| Jasmine 4.3 | Frontend testing framework |
| Karma 6.4 | Frontend test runner |

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** or higher
- **Node.js 18+** and npm
- **MySQL 8.x** (or Docker for containerized setup)
- **Maven** (wrapper included in project)

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/thibautlfr-studylab/oc-p06-full-stack-app.git
cd oc-p06-full-stack-app
```

### 2. Database Setup

#### Option A: Using Docker (Recommended)

```bash
make db-up
```

This starts a MySQL 8.0 container with:
- Database name: `mdd`
- Port: `3306`
- Root password: `root`
- Auto-initialization with schema and fixtures

#### Option B: Manual MySQL Setup

1. Create a MySQL database:
```sql
CREATE DATABASE mdd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Run the initialization scripts:
```bash
mysql -u root -p mdd < resources/sql/schema.sql
mysql -u root -p mdd < resources/sql/fixtures.sql
```

### 3. Backend Setup

```bash
cd back
./mvnw spring-boot:run
```

The API server starts at `http://localhost:9000`

### 4. Frontend Setup

```bash
cd front
npm install
npm start
```

The development server starts at `http://localhost:4200`

---

## Usage

### Running the Application

| Service | Command | URL |
|---------|---------|-----|
| Database | `make db-up` | `localhost:3306` |
| Backend | `cd back && ./mvnw spring-boot:run` | `http://localhost:9000` |
| Frontend | `cd front && npm start` | `http://localhost:4200` |
| Swagger UI | - | `http://localhost:9000/swagger-ui.html` |

### Quick Start with Make

```bash
# Install all dependencies
make install

# Start database
make db-up

# Then in separate terminals:
cd back && ./mvnw spring-boot:run
cd front && npm start
```

---

## Available Make Commands

| Command | Description |
|---------|-------------|
| `make help` | Display available commands |
| `make install` | Install frontend and backend dependencies |
| `make clean` | Clean dependencies and generated files |
| `make test` | Run all tests (frontend + backend) |
| `make test-front` | Run frontend tests (headless Chrome) |
| `make test-back` | Run backend tests |
| `make db-up` | Start MySQL database (Docker) |
| `make db-down` | Stop MySQL database |
| `make db-reset` | Reset database (removes all data) |

---

## Testing

### Run All Tests

```bash
make test
```

### Backend Tests

```bash
make test-back
# or
cd back && ./mvnw test
```

Coverage reports are generated at `back/target/site/jacoco/index.html`

### Frontend Tests

```bash
make test-front
# or
cd front && npm test -- --watch=false --browsers=ChromeHeadless
```

### Run Specific Test

```bash
# Backend - specific test class
cd back && ./mvnw test -Dtest=AuthControllerTest

# Frontend - specific spec file
cd front && ng test --include=**/topic.component.spec.ts
```

---

## API Documentation

### Swagger UI

Once the backend is running, access the interactive API documentation at:

```
http://localhost:9000/swagger-ui.html
```

### OpenAPI Specification

```
http://localhost:9000/v3/api-docs
```

### Postman Collection

A Postman collection is available at `resources/postman/MDD_API.postman_collection.json`

---

## Contributing

This project follows **Gitflow** workflow:

| Branch | Purpose |
|--------|---------|
| `main` | Production releases |
| `develop` | Integration branch |
| `feat/*` | New features |
| `release/*` | Release preparation |
| `test/*` | Test implementations |

### Branch Naming

- `feat/user-authentication` - New feature
- `fix/login-validation` - Bug fix
- `test/backend-unit` - Test coverage
- `refactor/service-layer` - Code refactoring
- `docs/api-updates` - Documentation changes