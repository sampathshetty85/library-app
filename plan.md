# plan.md — Library App Learning Plan

A phase-by-phase roadmap. Each phase is taught concept-first, code second.
Status: ✅ Done | 🔄 In Progress | ⬜ Not Started

---

## Phase 1 — Core Java & OOP Concepts
> Domain: plain Java classes — no frameworks yet.

| Step | Concept              | File(s)                          | Status |
|------|----------------------|----------------------------------|--------|
| 1    | Encapsulation        | `model/Book.java`, `Main.java`   | ✅ Done |
| 2    | Abstraction          | `model/LibraryItem.java` (interface) | ✅ Done |
| 3    | Inheritance          | `AbstractLibraryItem`, `Magazine.java` | ✅ Done |
| 4    | Polymorphism         | `model/Library.java`, `Main.java` | ✅ Done |
| 5    | Modular packages     | `repository/`, `service/`, `api/` | ✅ Done |

**Phase 1 complete. Resume point → Phase 2, Step 1: Spring Boot bootstrap**

---

## Phase 2 — REST API (Spring Boot)
> Expose the Library over HTTP. Phase 1 plain-Java stubs get replaced with real Spring annotations.

| Step | Concept              | File(s)                          | Status |
|------|----------------------|----------------------------------|--------|
| 1    | Spring Boot bootstrap | `pom.xml`, `LibraryApplication.java` | ✅ Done |
| 2    | Controller + routes  | `api/BookController.java` (real HTTP) | ✅ Done |
| 3    | Request/response body | JSON in/out, `@RequestBody`     | ✅ Done |

**Phase 2 complete ✅ → Resume point: Phase 3, Step 1: PostgreSQL + JPA setup**

---

## Phase 3 — Database Layer (PostgreSQL + Spring Data JPA)
> Data that survives a restart. PostgreSQL runs in Docker (just to start it locally — full Docker tutorial is Phase 8). Spring Data JPA (backed by Hibernate) translates Java objects ↔ SQL rows automatically. JDBC still runs underneath — JPA just spares you from writing it by hand.

**Why Docker here?** PostgreSQL is a separate server process. Docker is the easiest way to run it locally without installing it. Think of it as: `docker run postgres` = instant database server, no manual install. We'll learn *how* Docker works properly in Phase 8.

**Start PostgreSQL (one command):**
```bash
docker run --name library-db \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=librarydb \
  -p 5432:5432 -d postgres
```

| Step | Concept              | File(s)                          | Status |
|------|----------------------|----------------------------------|--------|
| 1    | Add JPA + PostgreSQL driver to pom.xml | `pom.xml`, `application.properties` | ✅ Done |
| 2    | Entity & table mapping | `@Entity`, `@Inheritance(JOINED)` on model classes | ✅ Done |
| 3    | JPA Repository interface | Replace `BookRepository` ArrayList with `JpaRepository` | ✅ Done |
| 4    | Wire & test | Books survive a server restart   | ✅ Done |

**Phase 3 complete ✅ → Resume point: Phase 4, Step 1: React frontend (Vite scaffold)**

## Phase 4 — React Frontend ✅ COMPLETE
> Browser UI calling the backend over HTTP.

| Step | Concept              | File(s)                          | Status |
|------|----------------------|----------------------------------|--------|
| 1    | Vite React scaffold  | `frontend/`                      | ✅ Done |
| 2    | Book list component  | `BookList.jsx`                   | ✅ Done |
| 3    | Add book form        | `AddBook.jsx`                    | ✅ Done |
| 4    | fetch() API calls    | Connect to Spring Boot endpoints | ✅ Done |

**Phase 4 complete ✅ → Resume point: Phase 5, Step 1: SLF4J logging**

---

## Phase 5 — Logging & Monitoring ✅ COMPLETE
| Step | Concept              | Status |
|------|----------------------|--------|
| 1    | SLF4J INFO/DEBUG/ERROR logging | ✅ Done |
| 2    | Spring Actuator health endpoint | ✅ Done |

**Phase 5 complete ✅ → Resume point: Phase 6, Step 1: pom.xml line by line**

---

## Phase 6 — Build Tooling ✅ COMPLETE
| Step | Concept              | Status |
|------|----------------------|--------|
| 1    | `pom.xml` line by line | ✅ Done |
| 2    | Maven lifecycle: compile → test → package | ✅ Done |
| 3    | `package.json` for frontend | ✅ Done |

**Phase 6 complete ✅ → Resume point: Phase 7, Step 2: Controller tests (integration)**

---

## Phase 7 — Testing ✅ COMPLETE
| Step | Concept              | Status |
|------|----------------------|--------|
| 1    | Unit tests (JUnit + Mockito) — BookServiceTest (4 tests) | ✅ Done |
| 1b   | JaCoCo test coverage report | ✅ Done |
| 2    | Integration tests — BookController with MockMvc | ✅ Done |
| 3    | Regression via refactor | ✅ Done |
| 4    | Basic React component test (optional) | ⬜ skipped |

---

## Phase 8 — Docker (Containerisation Tutorial) ✅ COMPLETE
> How Docker works, why it exists, and how to package the app properly.

| Step | Concept              | Status |
|------|----------------------|--------|
| 1    | What is Docker? Images, containers, layers — with analogies | ✅ Done |
| 2    | Backend Dockerfile — multi-stage build (builder + runner) | ✅ Done |
| 3    | Frontend Dockerfile — build a React/Nginx image | ✅ Done |
| 4    | Docker Compose — run backend + frontend + PostgreSQL together with one command | ✅ Done |
| 5    | Volumes — how PostgreSQL data persists across container restarts | ✅ Done |
| 6    | Networking — how containers talk to each other | ✅ Done |

---

## Phase 9 — Kubernetes with Minikube (Persistence Layer in K8s)
> Run the full app in a local Kubernetes cluster. Learn how K8s manages containers, storage, and networking.

| Step | Concept              | Status |
|------|----------------------|--------|
| 1    | What is Kubernetes? Pods, Deployments, Services — with analogies | ✅ Done |
| 2    | Minikube setup — start a local K8s cluster | ✅ Done |
| 3    | Deploy backend — Deployment + Service manifest | ✅ Done |
| 4    | Deploy frontend — Deployment + Service manifest | ✅ Done |
| 5    | Deploy PostgreSQL in K8s — StatefulSet | ✅ Done |
| 6    | Persistence in K8s — PersistentVolume + PersistentVolumeClaim (so DB data survives pod restarts) | ✅ Done |
| 7    | ConfigMap + Secret — pass DB credentials safely | ✅ Done |
| 8    | Kustomize — base manifests, dev overlay, prod overlay | ✅ Done |

---

## Phase 10 — Helm or ArgoCD ✅ COMPLETE
> Decision: ArgoCD chosen — native Kustomize support, GitOps pattern, suits a single-team self-hosted cluster.

| Step | Concept | Status |
|------|---------|--------|
| 1 | Choose: Helm chart vs ArgoCD Application | ✅ Done (ArgoCD chosen) |
| 2 | Install ArgoCD in Minikube | ✅ Done |
| 3 | Connect `github.tools.sap` repo with PAT credential | ✅ Done |
| 4 | Create ArgoCD Application pointing at `k8s/overlays/dev` | ✅ Done |
| 5 | GitOps proof — `git push` scales replicas without `kubectl` | ✅ Done |

---

## Phase 11 — Reference Handbook ✅ COMPLETE
> A ready-reference document covering every technical concept, code pattern, and decision made across all phases. Intended for interview prep and future projects.
> File: `reference-handbook.md`

| Step | Concept              | Status |
|------|----------------------|--------|
| 1    | Java & OOP — encapsulation, abstraction, inheritance, polymorphism with code examples | ✅ Done |
| 2    | Spring Boot — annotations, DI, REST, JPA, Actuator with code snippets | ✅ Done |
| 3    | React & JavaScript — JSX, useState, useEffect, fetch(), controlled inputs, props/callbacks | ✅ Done |
| 4    | Build tooling — pom.xml, Maven lifecycle, package.json, npm scripts, fat JAR vs dist/ | ✅ Done |
| 5    | Database — JPA, Hibernate, JOINED inheritance, JpaRepository, derived queries | ✅ Done |
| 6    | Docker & Kubernetes — images, containers, Deployments, Services, PVC, Secrets | ✅ Done |
| 7    | Full architecture diagram with every layer explained | ✅ Done |

---

## Architecture Diagram (current — Phase 4 complete)

```
Browser (port 5173)                        Spring Boot (port 8080)
┌──────────────────────────────────┐       ┌───────────────────────────────────┐
│  index.html                      │       │  @CrossOrigin(localhost:5173)      │
│  └─ <div id="root">              │       │  BookController                    │
│       │                          │       │  GET /books  → 200 + JSON array    │
│  main.jsx                        │       │  POST /books ← JSON body → 201     │
│  └─ renders <App />              │       │       │                            │
│       │                          │       │  BookService                       │
│  App.jsx                         │       │       │  save() / findAll()        │
│  ├─ <AddBook onBookAdded={...} />│──POST▶│  BookRepository                   │
│  │   controlled inputs           │◀─201──│       extends JpaRepository        │
│  │   JSON.stringify → fetch()    │       │       findByTitle() derived query  │
│  │   onSubmit → refreshKey++     │       │       │                            │
│  │                               │       │  Hibernate (JPA)                   │
│  └─ <BookList key={refreshKey} />│──GET─▶│       generates INSERT/SELECT/JOIN │
│      useEffect → fetch()         │◀─JSON─│       │                            │
│      books.map → <li> per book   │       │  HikariCP → JDBC                   │
│                                  │       │       │                            │
└──────────────────────────────────┘       │  PostgreSQL (Docker, port 5432)    │
     Vite dev server (hot-reload)          │  ├── abstract_library_item         │
                                           │  ├── book  (JOINED FK)             │
                                           │  └── magazine (JOINED FK)          │
                                           └───────────────────────────────────┘
```
