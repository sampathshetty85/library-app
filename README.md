# Library App

A full-stack library management app built as a step-by-step Java learning project — covering OOP, REST APIs, databases, React frontend, testing, and Docker.

## Stack

| Layer | Technology |
|---|---|
| Language | Java 24 (OpenJDK / SapMachine) |
| Backend | Spring Boot 4 (REST API) |
| Database | PostgreSQL (via Docker) |
| ORM | Spring Data JPA (Hibernate) |
| Build | Maven 3.9 |
| Frontend | React (Vite) |
| Testing | JUnit 5 + Mockito + JaCoCo |
| Logging | SLF4J + Spring Actuator |

## Project Structure

```
library-app/
├── src/
│   ├── main/java/com/library/
│   │   ├── LibraryApplication.java     ← Spring Boot entry point
│   │   ├── model/                      ← Book, Magazine, AbstractLibraryItem
│   │   ├── api/                        ← REST controllers + DTOs
│   │   ├── service/                    ← Business logic
│   │   └── repository/                 ← Spring Data JPA repositories
│   └── test/java/com/library/          ← JUnit 5 + MockMvc tests
├── frontend/                           ← React (Vite) app
├── pom.xml
└── CLAUDE.md
```

## Quick Start

### Prerequisites
- Java 24
- Maven 3.9+
- Docker (for PostgreSQL)
- Node.js 20+ (for frontend)

### 1. Start PostgreSQL
```bash
docker run -d \
  --name library-db \
  -e POSTGRES_DB=librarydb \
  -e POSTGRES_USER=library \
  -e POSTGRES_PASSWORD=library \
  -p 5432:5432 \
  postgres:16
```

### 2. Run the backend
```bash
mvn spring-boot:run
```

API available at `http://localhost:8080`

### 3. Run the frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend available at `http://localhost:5173`

## API Reference

### Books

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/books` | List all books |
| `POST` | `/books` | Add a new book |

**Add a book:**
```bash
curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dune","author":"Frank Herbert","pageCount":412}'
```

### Health
```bash
curl http://localhost:8080/actuator/health
```

## Testing

```bash
mvn test                    # run all tests
mvn test jacoco:report      # generate coverage report → target/site/jacoco/
```

## Learning Phases

| Phase | Topic | Status |
|---|---|---|
| 1 | Core Java & OOP (Encapsulation, Abstraction, Inheritance, Polymorphism) | ✅ Done |
| 2 | REST API with Spring Boot | ✅ Done |
| 3 | Database Layer (PostgreSQL + Spring Data JPA) | ✅ Done |
| 4 | React Frontend | ✅ Done |
| 5 | Logging & Monitoring (SLF4J + Actuator) | ✅ Done |
| 6 | Build Tooling (Maven + npm) | ✅ Done |
| 7 | Testing (JUnit 5, Mockito, MockMvc, JaCoCo) | 🔄 In Progress |
| 8 | Docker & Containerization | ⬜ Upcoming |
| 9 | Kubernetes (Minikube + Kustomize) | ⬜ Upcoming |
