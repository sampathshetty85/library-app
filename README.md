# Library App

A full-stack library management app built as a step-by-step Java learning project — covering every layer of modern software delivery: OOP, REST APIs, databases, React frontend, testing, Docker, Kubernetes, and GitOps with ArgoCD.

## Stack

| Layer | Technology |
|---|---|
| Language | Java 24 (OpenJDK) |
| Backend | Spring Boot (REST API) |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA (Hibernate) |
| Build | Maven 3.9 |
| Frontend | React + Vite |
| Styling | Plain CSS |
| Testing | JUnit 5 + Mockito + MockMvc + JaCoCo |
| Logging | SLF4J + Spring Actuator |
| Container | Docker + Docker Compose |
| Orchestration | Kubernetes (Minikube) + Kustomize |
| GitOps | ArgoCD |

## Architecture

```
Browser
  └─ React (Vite / Nginx)
       └─ /api/* → Nginx reverse proxy
            └─ Spring Boot (port 8080)
                 └─ Spring Data JPA (Hibernate)
                      └─ PostgreSQL 16
```

In Kubernetes, the frontend pod (Nginx) proxies `/api/` requests to the backend Service internally — the browser never talks to the backend directly.

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
│   ├── src/
│   │   ├── App.jsx
│   │   ├── BookList.jsx
│   │   └── AddBook.jsx
│   ├── Dockerfile                      ← Multi-stage build → Nginx
│   └── nginx.conf                      ← Reverse proxy config
├── k8s/
│   ├── base/                           ← Kustomize base manifests
│   └── overlays/
│       ├── dev/                        ← Dev overlay (1 replica)
│       └── prod/                       ← Prod overlay (3 replicas)
├── Dockerfile                          ← Multi-stage backend build
├── docker-compose.yml                  ← Full stack local dev
└── pom.xml
```

## Quick Start

### Option 1 — Docker Compose (easiest)

```bash
docker-compose up --build
```

- Frontend: `http://localhost`
- Backend API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

### Option 2 — Local dev (bare metal)

**Prerequisites:** Java 24, Maven 3.9+, Docker, Node.js 20+

```bash
# 1. Start PostgreSQL
docker run -d --name library-db \
  -e POSTGRES_DB=librarydb \
  -e POSTGRES_PASSWORD=secret \
  -p 5432:5432 postgres:16

# 2. Start backend
mvn spring-boot:run

# 3. Start frontend (separate terminal)
cd frontend && npm install && npm run dev
```

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`

### Option 3 — Kubernetes (Minikube)

```bash
# Start cluster
minikube start --driver=docker

# Build images into Minikube
minikube image build -t library-backend:latest .
minikube image build -t library-frontend:latest ./frontend

# Deploy with Kustomize
kubectl apply -k k8s/overlays/dev

# Open in browser
minikube service frontend --url
```

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/books` | List all books |
| `POST` | `/books` | Add a new book |
| `GET` | `/actuator/health` | Health check |

**Add a book:**
```bash
curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dune","author":"Frank Herbert","pageCount":412}'
```

## Testing

```bash
mvn test                    # run all tests + JaCoCo coverage
# coverage report → target/site/jacoco/index.html
```

| Test | Type | File |
|---|---|---|
| BookServiceTest | Unit (Mockito) | `BookServiceTest.java` |
| BookControllerTest | Integration (MockMvc) | `BookControllerTest.java` |

## Kubernetes — Key Concepts Used

| Resource | Used for |
|---|---|
| `Deployment` | Backend + Frontend (stateless, replaceable pods) |
| `StatefulSet` | PostgreSQL (stable pod name, sticky storage) |
| `Service (ClusterIP)` | Backend + Postgres (internal cluster routing) |
| `Service (NodePort)` | Frontend (externally accessible) |
| `PersistentVolumeClaim` | PostgreSQL data volume (survives pod restarts) |
| `ConfigMap` | DB URL + DB name |
| `Secret` | DB password (base64) |
| `Kustomize overlays` | Dev (1 replica) vs Prod (3 replicas) without duplicating YAML |

## Learning Phases

| Phase | Topic | Status |
|---|---|---|
| 1 | Core Java & OOP — Encapsulation, Abstraction, Inheritance, Polymorphism | ✅ Done |
| 2 | REST API with Spring Boot | ✅ Done |
| 3 | Database Layer — PostgreSQL + Spring Data JPA | ✅ Done |
| 4 | React Frontend — Vite, useState, useEffect, fetch() | ✅ Done |
| 5 | Logging & Monitoring — SLF4J + Spring Actuator | ✅ Done |
| 6 | Build Tooling — Maven lifecycle + npm scripts | ✅ Done |
| 7 | Testing — JUnit 5, Mockito, MockMvc, JaCoCo | ✅ Done |
| 8 | Docker — Dockerfile, multi-stage build, Compose, volumes, networking | ✅ Done |
| 9 | Kubernetes — Minikube, Deployments, Services, StatefulSet, PVC, ConfigMap, Secret, Kustomize | ✅ Done |
| 10 | GitOps — ArgoCD, sync policies, self-heal | 🔄 In Progress |
| 11 | Reference Handbook | ⬜ Upcoming |
