# CLAUDE.md — library-app

This is a teaching project. The goal is to learn Java and the full software delivery lifecycle step by step. Every phase is taught concept-first before writing code.

## Teaching Style Rules
- Explain each concept with a plain-language analogy before writing code
- After every code change, explain what each new file/block does
- Show/update the block diagram of the whole system after every step
- Keep each step small — the learner should be able to explain it back
- Audience: 9th grader, no prior programming background
- No jargon without a definition the first time
- Go phase by phase, in order — do not skip ahead

## Project: Library App
A tiny full-stack app: React frontend → Spring Boot backend → H2 database.
Domain: Book, Member, Library (intentionally small).

## Tech Stack
| Layer      | Technology                        |
|------------|-----------------------------------|
| Language   | Java 24 (OpenJDK / SapMachine)    |
| Backend    | Spring Boot (REST API)            |
| Database   | PostgreSQL (via Docker)           |
| ORM        | Spring Data JPA (Hibernate)       |
| Build      | Maven 3.9.11                      |
| Frontend   | React (Vite)                      |
| Testing    | JUnit 5 + Mockito                 |
| Logging    | SLF4J                             |
| Container  | Docker (images, Compose, volumes) |
| K8s config | Minikube + Kustomize overlays     |
| Deploy     | Helm or ArgoCD (TBD)              |

## Folder Structure (target)
```
library-app/
├── src/
│   ├── main/java/com/library/
│   │   ├── Main.java               ← entry point
│   │   ├── model/                  ← data blueprints (Book, Member, etc.)
│   │   ├── service/                ← business logic
│   │   ├── api/                    ← REST controllers
│   │   └── repository/             ← database access
│   └── test/java/com/library/      ← tests
├── frontend/                       ← React app (separate module)
├── pom.xml                         ← Maven build file
├── CLAUDE.md                       ← this file
├── plan.md                         ← phase-by-phase learning plan
└── build-log.md                    ← what was built and when
```

## Current Progress (last updated 2026-08-03)

### Phase 1 — Core Java & OOP ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | Encapsulation | ✅ Done |
| 2 | Abstraction | ✅ Done |
| 3 | Inheritance | ✅ Done |
| 4 | Polymorphism | ✅ Done |
| 5 | Modular packages | ✅ Done |

### Phase 2 — REST API (Spring Boot) ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | Spring Boot bootstrap (`pom.xml`, `LibraryApplication.java`) | ✅ Done |
| 2 | Controller + routes (`@RestController`, `@GetMapping`, `@PostMapping`) | ✅ Done |
| 3 | Request/response body (`@RequestBody`, DTO, `ResponseEntity`) | ✅ Done |

### Phase 3 — Database Layer (PostgreSQL + Spring Data JPA) ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | Add JPA + PostgreSQL driver, `application.properties` | ✅ Done |
| 2 | `@Entity`, `@Inheritance(JOINED)` on model classes | ✅ Done |
| 3 | `BookRepository` → `JpaRepository<Book, Long>` | ✅ Done |
| 4 | Data survives server restart | ✅ Done |

### Phase 4 — React Frontend ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | Vite React scaffold (`frontend/`, `index.html`, `main.jsx`, `App.jsx`) | ✅ Done |
| 2 | BookList component (`useState`, `useEffect`, `fetch GET /books`, `@CrossOrigin`) | ✅ Done |
| 3 | AddBook form (controlled inputs, `fetch POST /books`, callback prop, refreshKey) | ✅ Done |
| 4 | fetch() API calls end-to-end | ✅ Done |

### Phase 5 — Logging & Monitoring ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | SLF4J logging — `LoggerFactory`, INFO/DEBUG/WARN in BookService + BookController | ✅ Done |
| 2 | Spring Actuator — `/actuator/health` shows db/disk/liveness/readiness all UP | ✅ Done |

### Phase 6 — Build Tooling ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | `pom.xml` — parent, groupId/artifactId, properties, dependencies, scope, plugins | ✅ Done |
| 2 | Maven lifecycle — compile → test → package → clean; fat JAR (54 MB) | ✅ Done |
| 3 | `package.json` — scripts, dependencies vs devDependencies, `^` ranges, package-lock.json, `npm run build` → `dist/` | ✅ Done |

### Phase 7 — Testing ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | Unit tests — JUnit 5 + Mockito; BookServiceTest (4 tests); `spring-boot-starter-test` added to pom.xml | ✅ Done |
| 1b | JaCoCo coverage report — `mvn test` generates HTML at `target/site/jacoco/` | ✅ Done |
| 2 | Integration tests — BookController with MockMvc | ✅ Done |
| 3 | Regression via refactor | ✅ Done |
| 4 | Basic React component test (optional) | ⬜ skipped |

### Phase 8 — Docker ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | What is Docker? Images, containers, layers | ✅ Done |
| 2 | Backend Dockerfile — multi-stage build | ✅ Done |
| 3 | Frontend Dockerfile — React/Nginx image | ✅ Done |
| 4 | Docker Compose — run everything with one command | ✅ Done |
| 5 | Volumes — PostgreSQL data persists across restarts | ✅ Done |
| 6 | Networking — how containers talk to each other | ✅ Done |

### Phase 9 — Kubernetes with Minikube ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | What is Kubernetes? Pods, Deployments, Services | ✅ Done |
| 2 | Minikube setup | ✅ Done |
| 3 | Deploy backend — Deployment + Service manifest | ✅ Done |
| 4 | Deploy frontend — Deployment + Service manifest | ✅ Done |
| 5 | Deploy PostgreSQL — StatefulSet | ✅ Done |
| 6 | PersistentVolume + PersistentVolumeClaim | ✅ Done |
| 7 | ConfigMap + Secret | ✅ Done |
| 8 | Kustomize overlays | ✅ Done |

### Phase 10 — Helm or ArgoCD ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | Helm vs ArgoCD tradeoff — chose ArgoCD | ✅ Done |
| 2 | Install ArgoCD in Minikube (`argocd` namespace) | ✅ Done |
| 3 | Connect `github.tools.sap` repo with PAT credential | ✅ Done |
| 4 | Create ArgoCD Application → `k8s/overlays/dev` | ✅ Done |
| 5 | GitOps proof — `git push` auto-scales backend replicas | ✅ Done |

**Phase 10 complete ✅ → Phase 11 complete ✅ — All phases done!**

### Phase 11 — Reference Handbook ✅ COMPLETE
| Step | Concept | Status |
|------|---------|--------|
| 1 | Java & OOP — encapsulation, abstraction, inheritance, polymorphism | ✅ Done |
| 2 | Spring Boot — annotations, DI, REST, JPA, Actuator | ✅ Done |
| 3 | React & JavaScript — JSX, useState, useEffect, fetch(), props | ✅ Done |
| 4 | Build tooling — pom.xml, Maven lifecycle, package.json, npm scripts | ✅ Done |
| 5 | Database — JPA, Hibernate, JOINED inheritance, JpaRepository | ✅ Done |
| 6 | Docker & Kubernetes — images, containers, Deployments, Services, PVC, Secrets | ✅ Done |
| 7 | Full architecture diagram with every layer explained | ✅ Done |

**File: `reference-handbook.md` at project root**

### Files created so far
| File | Purpose |
|------|---------|
| `pom.xml` | Maven build file — Spring Boot 4.0.7 parent, spring-boot-starter-web, spring-boot-maven-plugin |
| `src/main/java/com/library/LibraryApplication.java` | Spring Boot entry point — `@SpringBootApplication`, starts Tomcat on port 8080 |
| `src/main/java/com/library/model/LibraryItem.java` | Interface (contract) |
| `src/main/java/com/library/model/AbstractLibraryItem.java` | Abstract parent (shared fields: title, available) |
| `src/main/java/com/library/model/Book.java` | Child class — adds author, pageCount, getSummary() |
| `src/main/java/com/library/model/Magazine.java` | Child class — adds publisher, issueNumber, getSummary() |
| `src/main/java/com/library/model/Library.java` | Holds List&lt;LibraryItem&gt;, printAllItems() — polymorphism demo |
| `src/main/resources/application.properties` | DB connection config — datasource URL, JPA settings |
| `src/main/java/com/library/model/AbstractLibraryItem.java` | `@Entity` + `@Inheritance(JOINED)` — parent table, holds id/title/available |
| `src/main/java/com/library/model/Book.java` | `@Entity @Table("book")` — child table, author + page_count |
| `src/main/java/com/library/model/Magazine.java` | `@Entity @Table("magazine")` — child table, publisher + issue_number |
| `src/main/java/com/library/repository/BookRepository.java` | `JpaRepository<Book, Long>` interface — Spring generates all CRUD + findByTitle() |
| `src/main/java/com/library/api/BookRequest.java` | DTO — carries title/author/pageCount from HTTP request body into the service layer |
| `src/main/java/com/library/api/BookController.java` | `@RestController` + `@CrossOrigin` — GET /books (200), POST /books with JSON body (201) |
| `frontend/src/main.jsx` | React entry point — mounts `<App />` into `<div id="root">` |
| `frontend/src/App.jsx` | Root component — renders `<AddBook>` + `<BookList>`, owns `refreshKey` state |
| `frontend/src/BookList.jsx` | Fetches `GET /books` on mount, renders `<li>` per book |
| `frontend/src/AddBook.jsx` | Controlled form — POST /books on submit, calls `onBookAdded` callback to refresh list |

```bash
# Start the server (Phase 2+)
mvn spring-boot:run

# Test endpoints
curl http://localhost:8080/books
curl -X POST -H "Content-Type: application/json" \
  -d '{"title":"Dune","author":"Frank Herbert","pageCount":412}' \
  http://localhost:8080/books
```

## Environment (verified 2026-07-28)
- Java: OpenJDK 24 (SapMachine)
- Maven: 3.9.11
- Node.js: v24.6.0
- npm: 11.5.1
- Docker: 28.3.3
- kubectl + Kustomize: v1.33.4 / v5.6.0
- Gradle: not installed (not needed)
