# build-log.md — Library App

Chronological log of every step taken, what was built, and what was learned.

---

## 2026-07-28

### Environment Check
Verified all required tools are present:
- Java 24 (OpenJDK SapMachine) ✅
- Maven 3.9.11 ✅
- Node.js v24.6.0 + npm 11.5.1 ✅
- Docker 28.3.3 ✅
- kubectl v1.33.4 + Kustomize v5.6.0 ✅
- Gradle: not installed (not needed — using Maven)

---

### Phase 1, Step 1 — Encapsulation ✅

**Concept taught:** Encapsulation — hiding internal data behind `private` fields and exposing it only through controlled getters/setters.

**Analogy used:** Vending machine — you press buttons (getters/setters), you can't reach inside and grab things directly.

**Files created:**
| File | Purpose |
|------|---------|
| `src/main/java/com/library/model/Book.java` | First class — blueprint for a Book with private fields, getters, one setter with a guard, and a `getSummary()` method |
| `src/main/java/com/library/Main.java` | Entry point — creates two Book objects, calls methods, demonstrates the setter guard rejecting a negative page count |

**Compile & run command used:**
```bash
javac -d out src/main/java/com/library/model/Book.java src/main/java/com/library/Main.java
java -cp out com.library.Main
```

**Output observed:**
```
"The Alchemist" by Paulo Coelho (208 pages)
"Atomic Habits" by James Clear (320 pages)
Updated: "The Alchemist" by Paulo Coelho (210 pages)
After bad update: "Atomic Habits" by James Clear (320 pages)
```

**Key takeaway:** The `setPageCount(-5)` call was silently rejected by the guard in the setter — demonstrating that encapsulation protects object integrity.

---

## 2026-07-29

### Phase 1, Step 2 — Abstraction ✅

**Concept taught:** Abstraction — defining *what* something must do via an interface (contract), without specifying *how*.

**Analogy used:** Job description vs employee — the interface lists the requirements, the class provides the actual work.

**Files created/updated:**
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/com/library/model/LibraryItem.java` | Created | Interface with 3 method signatures: `getTitle()`, `getSummary()`, `isAvailable()` |
| `src/main/java/com/library/model/Book.java` | Updated | Added `implements LibraryItem`, `available` field, `isAvailable()`, `@Override` annotations |
| `src/main/java/com/library/Main.java` | Updated | Variable declared as `LibraryItem` type — showed coding against the contract, not the concrete type |

**Key takeaway:** Declaring a variable as `LibraryItem item = new Book(...)` means the rest of the code only cares about the contract — not whether the object is a Book, Magazine, or anything else.

---

### Phase 1, Step 3 — Inheritance ✅

**Concept taught:** Inheritance — putting shared code in a parent `abstract class` so child classes get it for free via `extends`.

**Analogy used:** Half-built house — the foundation and walls (shared fields) are done; each buyer (child class) finishes their own rooms.

**Files created/updated:**
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/com/library/model/AbstractLibraryItem.java` | Created | Abstract parent class — holds `title`, `available`, `getTitle()`, `isAvailable()`, `setAvailable()` |
| `src/main/java/com/library/model/Book.java` | Updated | Now `extends AbstractLibraryItem` — stripped of shared fields, calls `super(title)` in constructor |
| `src/main/java/com/library/model/Magazine.java` | Created | Second child class — `extends AbstractLibraryItem`, adds `publisher`, `issueNumber`, own `getSummary()` |
| `src/main/java/com/library/Main.java` | Updated | Creates both Book and Magazine, calls shared methods on both |

**Compile & run command used:**
```bash
javac -d out \
  src/main/java/com/library/model/LibraryItem.java \
  src/main/java/com/library/model/AbstractLibraryItem.java \
  src/main/java/com/library/model/Book.java \
  src/main/java/com/library/model/Magazine.java \
  src/main/java/com/library/Main.java
java -cp out com.library.Main
```

**Output observed:**
```
"The Alchemist" by Paulo Coelho (208 pages)
Available: true
"National Geographic" — Issue #245 by Nat Geo Partners
Available: true

After checkout:
The Alchemist available: false
National Geographic available: true
```

**Key takeaway:** `Book` and `Magazine` share `title`/`available` behaviour written once in the parent. `super(title)` hands off parent setup to `AbstractLibraryItem`. `abstract` prevents anyone from creating a raw `AbstractLibraryItem` object directly.

---

---

### Phase 1, Step 4 — Polymorphism ✅

**Concept taught:** Polymorphism — one method call, many behaviours. Java automatically calls the right `getSummary()` at runtime depending on the actual object type.

**Analogy used:** Teacher asks every student to "introduce yourself" — same instruction, different responses. No need for the teacher to check who each student is first.

**New Java concepts introduced:**
- `List<LibraryItem>` — a typed collection (generic) that holds any LibraryItem
- `ArrayList` — Java's standard resizable list implementation
- For-each loop — `for (LibraryItem item : items)`
- Dynamic dispatch — Java routing a method call to the right implementation at runtime

**Files created/updated:**
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/com/library/model/Library.java` | Created | Holds `List<LibraryItem>`, `addItem()`, `printAllItems()` — polymorphism in action |
| `src/main/java/com/library/Main.java` | Updated | Adds Books and Magazines to the Library, calls `printAllItems()` |

**Output observed:**
```
=== City Central Library ===
"The Alchemist" by Paulo Coelho (208 pages)
  Available: true
"Atomic Habits" by James Clear (320 pages)
  Available: true
"National Geographic" — Issue #245 by Nat Geo Partners
  Available: true
"Time" — Issue #99 by Time USA LLC
  Available: true

Total items: 4
```

**Key takeaway:** Same loop, same method call (`getSummary()`), different output per type — Java dispatches to the right version automatically. No `if/else instanceof` checks needed.

---

### Phase 1, Step 5 — Modular Packages ✅

**Concept taught:** Separation of concerns — split code into `model`, `repository`, `service`, `api` packages, each with one clear job. Data flows one way: `api → service → repository → model`.

**Analogy used:** Hospital departments — pharmacy, radiology, surgery each have one job and talk to each other through defined channels only.

**New Java concepts introduced:**
- `Optional<T>` — honest null-handling ("maybe a value, maybe empty")
- Dependency injection — passing tools in from outside via constructor instead of building them internally
- Method reference — `Book::getSummary` shorthand for `book -> book.getSummary()`
- `import` statements — registering the full package address of a class so you can use its short name

**Files created/updated:**
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/com/library/repository/BookRepository.java` | Created | Stores books in ArrayList, `save()`, `findAll()`, `findByTitle()` returning `Optional<Book>` |
| `src/main/java/com/library/service/BookService.java` | Created | Business logic — `addBook()`, `getAllBooks()`, `getBookSummary()`. Uses repository via constructor injection |
| `src/main/java/com/library/api/BookController.java` | Created | Plain Java stub for `handleGetAllBooks()` and `handlePostBook()`. Will become real HTTP controller in Phase 2 |
| `src/main/java/com/library/Main.java` | Updated | Wires all three layers manually: `repository → service → controller` |

**Compile & run command used:**
```bash
javac -d out \
  src/main/java/com/library/model/LibraryItem.java \
  src/main/java/com/library/model/AbstractLibraryItem.java \
  src/main/java/com/library/model/Book.java \
  src/main/java/com/library/model/Magazine.java \
  src/main/java/com/library/model/Library.java \
  src/main/java/com/library/repository/BookRepository.java \
  src/main/java/com/library/service/BookService.java \
  src/main/java/com/library/api/BookController.java \
  src/main/java/com/library/Main.java
java -cp out com.library.Main
```

**Output observed:**
```
POST /books → Added: "The Alchemist"
POST /books → Added: "Atomic Habits"
POST /books → Added: "Deep Work"

GET /books → 3 book(s) found:
  "The Alchemist" by Paulo Coelho (208 pages)
  "Atomic Habits" by James Clear (320 pages)
  "Deep Work" by Cal Newport (296 pages)

Summary lookup: "Atomic Habits" by James Clear (320 pages)
Missing lookup: Book not found: Unknown Book
```

**Key takeaway:** Each layer only talks to its immediate neighbour. `Main.java` wires them by hand — in Phase 2, Spring Boot will do this automatically via `@Autowired`.

---

## Phase 1 Complete ✅

All 5 OOP concepts covered using plain Java — no frameworks. The app now has a clean 3-layer architecture (api → service → repository) backed by an in-memory List.

---

## 2026-08-03

### Phase 2, Step 1 — Spring Boot Bootstrap ✅

**Concept taught:** Spring Boot as a framework that replaces manual wiring and starts a real embedded web server. Difference between Spring (raw framework) and Spring Boot (pre-configured wrapper).

**Analogy used:** Spring = engine parts on a workbench. Spring Boot = car that arrives pre-assembled, just turn the key.

**New concepts introduced:**
- `pom.xml` — Maven project descriptor: declares dependencies, identity, Java version, build plugins
- `<parent>` in pom.xml — inheriting Spring Boot's pre-selected compatible library versions
- `spring-boot-starter-web` — one dependency that pulls in Tomcat + Spring MVC + Jackson
- `@SpringBootApplication` — bundles `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`
- `SpringApplication.run()` — starts Tomcat, scans packages, keeps process alive
- Annotations explained: labels that attach metadata to code; only act when a tool reads them
- Spring vs Spring Boot distinction

**Files created:**
| File | Purpose |
|------|---------|
| `pom.xml` | Maven build file — Spring Boot 4.0.7 parent, spring-boot-starter-web dependency, spring-boot-maven-plugin |
| `src/main/java/com/library/LibraryApplication.java` | New entry point — `@SpringBootApplication`, `SpringApplication.run()` |

**Command used:**
```bash
mvn spring-boot:run
```

**Output observed:**
```
Tomcat initialized with port 8080 (http)
Root WebApplicationContext: initialization completed in 251 ms
Started LibraryApplication in 0.5 seconds
```

Browser at `http://localhost:8080` → Whitelabel Error Page (404) — server alive, no routes defined yet.

**Errors hit and fixed:**
| Error | Cause | Fix |
|-------|-------|-----|
| `No plugin found for prefix 'sprintboot'` | Typo in command | Use `spring-boot:run` with hyphen |
| `Unsupported class file major version 68` | Spring Boot 3.3.5 doesn't support Java 24 | Upgraded to Spring Boot 4.0.7 |
| `Port 8080 was already in use` | Stale background Java process | `lsof -i :8080 \| grep LISTEN` → `kill <PID>` |

**Key takeaway:** `pom.xml` is read by Maven (downloads libraries). `@SpringBootApplication` is read by Spring (scans and wires classes). They are separate concerns. `Main.java` is now unused — Spring Boot takes over entry point duties. Manual wiring in `Main.java` will be replaced by `@Autowired` in Step 2.

---

## Phase 2, Step 1 Complete ✅

**Next: Phase 2, Step 2 — REST Controller**
- Add `@RestController`, `@GetMapping("/books")`, `@PostMapping("/books")` to `BookController`
- Add `@Service` to `BookService`, `@Repository` to `BookRepository`
- Delete `Main.java` (replaced by Spring wiring)
- Test endpoints with browser and curl

---

## 2026-08-03

### Phase 2, Step 2 — REST Controller ✅

**Concept taught:** Turning a plain Java class into a real HTTP controller using Spring annotations. Spring auto-wires all three layers so `Main.java` is no longer needed.

**Analogy used:** `@Autowired` = facilities department. Instead of building your own tools (manual `new` calls), you declare what you need and Spring delivers it on day one.

**New concepts introduced:**
- `@RestController` — marks class as HTTP handler; return values auto-converted to JSON by Jackson
- `@RequestMapping("/books")` — base path prefix for all routes in the class
- `@GetMapping` — maps `GET /books` to a method
- `@PostMapping` — maps `POST /books` to a method
- `@RequestParam` — extracts query parameters from the URL (e.g. `?title=Dune&author=Herbert`)
- `@Service` — registers `BookService` as a Spring-managed component
- `@Repository` — registers `BookRepository` as a Spring-managed component
- `@Autowired` on constructor — Spring injects the required dependency automatically
- Jackson serialisation — `List<Book>` returned from a method becomes a JSON array with zero extra code

**Files changed:**
| File | Change |
|------|--------|
| `src/main/java/com/library/api/BookController.java` | Added `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@Autowired`; changed void methods to return values |
| `src/main/java/com/library/service/BookService.java` | Added `@Service`, `@Autowired` on constructor |
| `src/main/java/com/library/repository/BookRepository.java` | Added `@Repository` |
| `src/main/java/com/library/Main.java` | **Deleted** — Spring wiring replaces all manual `new` calls |

**Commands used:**
```bash
mvn spring-boot:run

curl -X POST "http://localhost:8080/books?title=The+Alchemist&author=Paulo+Coelho&pageCount=208"
curl -X POST "http://localhost:8080/books?title=Atomic+Habits&author=James+Clear&pageCount=320"
curl http://localhost:8080/books
```

**Output observed:**
```json
[
  {"title":"The Alchemist","author":"Paulo Coelho","pageCount":208,"available":true,"summary":"..."},
  {"title":"Atomic Habits","author":"James Clear","pageCount":320,"available":true,"summary":"..."}
]
```

**Key takeaway:** `@RestController` + `@GetMapping`/`@PostMapping` replace the manual `System.out.println` stubs. Spring injects all dependencies. Jackson converts Java objects to JSON automatically — no serialisation code written. Data is still in-memory; a server restart loses all books.

---

## Phase 2, Step 2 Complete ✅

**Next: Phase 2, Step 3 — Request/Response Body**
- Accept a JSON body on POST (not query params) using `@RequestBody`
- Return meaningful HTTP status codes (`ResponseEntity`)
- Test with curl sending JSON

---

## 2026-08-03

### Phase 2, Step 3 — Request/Response Body ✅

**Concept taught:** Real APIs send data in the request body as JSON, not in the URL as query parameters. DTOs decouple the API surface from the internal model. `ResponseEntity` gives explicit control over HTTP status codes.

**Analogy used:** DTO = order form. The caller fills in a simple form (title, author, pageCount). The kitchen (service layer) does its own internal work. The caller never sees internal complexity.

**New concepts introduced:**
- `@RequestBody` — tells Spring to read the HTTP request body and deserialise it from JSON into a Java object (Jackson does the conversion)
- DTO (Data Transfer Object) — a plain Java class whose only job is to carry data in/out of the API; no business logic, no inheritance
- Why not reuse `Book` directly — `Book` has internal complexity (extends `AbstractLibraryItem`, has `available`, `getSummary()`); the DTO exposes only what the caller needs to provide
- `ResponseEntity<T>` — wraps the return value with an explicit HTTP status code
- `ResponseEntity.ok(body)` — shorthand for `200 OK` with a body
- `ResponseEntity.status(201).build()` — `201 Created`, no body
- `Content-Type: application/json` header — tells Spring the request body is JSON so it knows how to deserialise it

**Files created/changed:**
| File | Action | Purpose |
|------|--------|---------|
| `src/main/java/com/library/api/BookRequest.java` | Created | DTO — plain class with title, author, pageCount fields + getters/setters for Jackson |
| `src/main/java/com/library/api/BookController.java` | Updated | `addBook()` now takes `@RequestBody BookRequest`; both methods return `ResponseEntity` |

**Command used:**
```bash
mvn spring-boot:run

curl -X POST -H "Content-Type: application/json" \
  -d '{"title":"Dune","author":"Frank Herbert","pageCount":412}' \
  http://localhost:8080/books

curl http://localhost:8080/books
```

**Output observed:**
```
POST /books → HTTP 201
GET  /books → HTTP 200
[{"title":"Dune","author":"Frank Herbert","pageCount":412,"available":true,...},
 {"title":"The Alchemist","author":"Paulo Coelho","pageCount":208,...}]
```

**Key takeaway:** `@RequestBody` + a DTO replaces `@RequestParam`. The URL stays clean. Jackson deserialises the JSON body automatically. `ResponseEntity` makes HTTP status codes explicit — `201` for create, `200` for read. `BookService` and `BookRepository` were untouched — the change was entirely in the API layer.

---

## Phase 2 Complete ✅

All 3 steps done. The app now has a proper REST API:
- `GET /books` → `200 OK` + JSON array
- `POST /books` with JSON body → `201 Created`
- Spring wires all layers automatically
- Data is still in-memory — lost on restart

**Next: Phase 3 — Database Layer (PostgreSQL + Spring Data JPA)**

---

## 2026-08-03

### Phase 3, Step 1 — PostgreSQL + JPA Setup ✅

**Concept taught:** Connecting Spring Boot to a real database. PostgreSQL runs in Docker. JPA/Hibernate sits between Java and the database. JDBC is the underlying wire.

**Analogy used:** JPA = UN translator. Java speaks Java, database speaks SQL, JPA sits in the middle so neither side has to learn the other's language.

**New concepts introduced:**
- `spring-boot-starter-data-jpa` — brings in Hibernate (JPA implementation) + Spring Data
- PostgreSQL JDBC driver (`scope=runtime`) — loaded by Hibernate at runtime, never referenced in code
- `application.properties` — runtime configuration file; keeps connection details out of code
- `spring.jpa.show-sql=true` — prints every Hibernate-generated SQL to the console
- `ddl-auto=update` — Hibernate creates/updates tables automatically from `@Entity` classes (dev only, never production)
- HikariCP — Spring Boot's built-in connection pool; reuses connections instead of opening a new one per request

**Files created/changed:**
| File | Action | Purpose |
|------|--------|---------|
| `pom.xml` | Updated | Added `spring-boot-starter-data-jpa` + `postgresql` driver |
| `src/main/resources/application.properties` | Created | DB connection URL, username, password, JPA settings |

**Command used:**
```bash
docker run --name library-db \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=librarydb \
  -p 5432:5432 -d postgres

mvn spring-boot:run
```

**Output observed:**
```
HikariPool-1 - Start completed
Started LibraryApplication in 1.125 seconds
```

**Key takeaway:** Spring Boot connected to PostgreSQL. Schema not yet created — no `@Entity` classes yet. That's Step 2.

---

### Phase 3, Step 2 — Entity & Table Mapping ✅

**Concept taught:** Teaching Hibernate about Java classes so it can create matching database tables. Chose JOINED inheritance strategy so both Book and Magazine scale cleanly with no NULLs.

**Three JPA inheritance strategies reviewed:**
| Strategy | Tables | Pro | Con |
|---|---|---|---|
| SINGLE_TABLE | 1 table, dtype column | Simplest, fastest | NULLs everywhere, can't enforce NOT NULL |
| TABLE_PER_CLASS | 1 table per concrete class | No NULLs | Shared fields duplicated, UNION for all-items query |
| JOINED (chosen) | 1 parent + 1 per child | Normalised, no NULLs, scales cleanly | JOIN on every read |

**New concepts introduced:**
- `@Entity` — marks a class as a JPA-managed database table
- `@Table(name = "...")` — explicit table name
- `@Inheritance(strategy = InheritanceType.JOINED)` — JOINED strategy on parent
- `@Id` — marks the primary key field
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` — database auto-assigns incrementing IDs
- `protected NoArgConstructor()` — required by JPA so Hibernate can instantiate entities via reflection
- `jakarta.persistence.*` — correct package in Spring Boot 4 (not `javax.persistence.*`)
- `@MappedSuperclass` — considered and rejected in favour of `@Entity` + JOINED for scalability

**Files changed:**
| File | Change |
|------|--------|
| `model/AbstractLibraryItem.java` | Added `@Entity`, `@Inheritance(JOINED)`, `@Table`, `@Id`, `@GeneratedValue`, `Long id`, no-arg constructor |
| `model/Book.java` | Added `@Entity`, `@Table("book")`, no-arg constructor |
| `model/Magazine.java` | Added `@Entity`, `@Table("magazine")`, no-arg constructor |

**SQL Hibernate generated automatically:**
```sql
create table abstract_library_item (id bigint generated by default as identity, available boolean not null, title varchar(255), primary key (id))
create table book (author varchar(255), page_count integer not null, id bigint not null, primary key (id))
create table magazine (issue_number integer not null, publisher varchar(255), id bigint not null, primary key (id))
alter table book add constraint ... foreign key (id) references abstract_library_item
alter table magazine add constraint ... foreign key (id) references abstract_library_item
```

**Key takeaway:** Hibernate read the `@Entity` annotations and created 3 tables with foreign key constraints — zero SQL written by hand. JOINED strategy means a full Book = JOIN of `abstract_library_item` + `book` on `id`.

---

### Phase 3, Step 3 — JPA Repository ✅

**Concept taught:** Replacing the in-memory ArrayList `BookRepository` with a `JpaRepository` interface. Spring generates the full implementation — save, findAll, findById, delete — at startup.

**New concepts introduced:**
- `JpaRepository<Book, Long>` — interface declaring entity type + primary key type; Spring generates all CRUD methods
- Derived query methods — `findByTitle(String title)` declared in interface; Spring generates the JOIN SQL automatically
- `Optional<Book>` — still used as return type; JPA returns `Optional.empty()` when not found

**Files changed:**
| File | Change |
|------|--------|
| `repository/BookRepository.java` | Converted from class with ArrayList to interface extending `JpaRepository<Book, Long>`; added `findByTitle()` derived query |

**Restart test:**
- Added "Dune" and "Atomic Habits" via POST
- Restarted server
- GET /books returned both books — data survived ✅

**Direct DB query:**
```sql
SELECT * FROM abstract_library_item;  -- id, available, title
SELECT * FROM book;                   -- author, page_count, id (FK)
```

**Key takeaway:** One interface declaration replaces ~30 lines of ArrayList code. Spring generates all SQL. Data now persists across server restarts because it's in PostgreSQL, not RAM.

---

### Conceptual discussions (2026-08-03)

**Topics covered beyond the code:**
- JDBC as JPA's predecessor — JDBC still runs underneath JPA; JPA is layered on top
- Where to see the DB — VS Code extension, pgAdmin, DBeaver, TablePlus; `docker exec psql` for raw queries
- Code vs data separation — `@Entity` defines schema shape (in code/git); actual data lives in Docker (separate)
- Multi-tenancy patterns — shared schema (tenant_id column), separate schemas per customer (SAP BTP approach), separate DB per customer (banks/healthcare)
- Production DB management — `ddl-auto=update` never in production; Flyway/Liquibase for schema migrations; DB team vs app team boundary
- How microservices find their DB — Kubernetes Secrets injected as env vars; `application.properties` reads `${DB_URL}`
- Consul — service discovery + KV config store; how services find each other in dynamic environments; Spring Cloud Consul integration; SAP BTP equivalents (Service Manager, Credential Store, VCAP_SERVICES)

---

## Phase 3 Complete ✅

All 4 steps done:
- PostgreSQL running in Docker ✅
- 3 tables created by Hibernate (JOINED strategy) ✅
- JpaRepository replaces ArrayList ✅
- Data survives server restarts ✅

**Next: Phase 4 — React Frontend**

---

## 2026-08-04

### Phase 4, Step 1 — Vite React Scaffold ✅

**Concept taught:** The frontend is the browser-side of the app. React is a JavaScript library for building UIs that update automatically when data changes. Vite scaffolds a starter project and runs a hot-reload dev server.

**Analogy used:** Backend = kitchen, Frontend = dining room, API = waiter between them. Old websites = printed newspapers (full reload). React = live scoreboard (only the changed part updates). JSX = HTML with live JavaScript expressions embedded in `{}`.

**New concepts introduced:**
- `index.html` — the one HTML page; contains a single `<div id="root">` placeholder where React paints everything
- `main.jsx` — entry point; calls `createRoot().render(<App />)` to mount React into the root div
- `App.jsx` — root component; a JavaScript function that returns JSX (HTML-like markup with embedded JS)
- Component — a reusable piece of UI; a function that returns markup
- JSX — HTML-like syntax inside `.jsx` files; `{count}` embeds live JavaScript values
- `package.json` — frontend equivalent of `pom.xml`; lists JS dependencies and scripts
- Vite dev server — runs on port 5173, hot-reloads on every file save

**Commands used:**
```bash
npm create vite@latest frontend -- --template react
cd frontend && npm install
npm run dev   # starts dev server at http://localhost:5173
```

**Output observed:** Vite default demo page visible at http://localhost:5173

---

### Phase 4, Step 2 — BookList Component ✅

**Concept taught:** Fetching data from the backend on page load and rendering it as a list. Controlled state drives what the browser shows.

**Analogy used:** `useState` = a whiteboard React owns — it always shows exactly what you last wrote. `useEffect` with `[]` = an alarm that rings only when you first walk into a room (once on load, not on every re-render).

**New concepts introduced:**
- `useState([])` — declares state; returns `[currentValue, setterFunction]`; React re-renders component when setter is called
- `useEffect(() => { ... }, [])` — runs code after the component appears on screen; empty `[]` = run once on mount
- `fetch()` — browser built-in for making HTTP requests; returns a Promise
- `.then(res => res.json())` — parses the JSON response body
- `books.map(book => <li>...)` — loops over array and returns one JSX element per item
- `key={book.id}` — required by React to efficiently track which list item is which
- CORS — browser blocks requests across different ports (5173 → 8080) by default; `@CrossOrigin` on `BookController` tells Spring Boot to allow it

**Files created/changed:**
| File | Action |
|------|--------|
| `frontend/src/BookList.jsx` | Created — `useState`, `useEffect`, `fetch('GET /books')`, renders `<li>` per book |
| `frontend/src/App.jsx` | Replaced Vite demo — imports and renders `<BookList />` |
| `src/main/java/com/library/api/BookController.java` | Added `@CrossOrigin(origins = "http://localhost:5173")` |

**Output observed:** 3 books (previously added via curl) displayed in browser at http://localhost:5173 ✅

---

### Phase 4, Step 3 — AddBook Form ✅

**Concept taught:** Controlled inputs — React owns the input values via state. Form submission calls POST /books and then refreshes the list automatically.

**Analogy used:** Controlled component = a whiteboard you're the only one who can write on. You hold the marker (`setState`). The board (`value`) always shows exactly what you wrote.

**New concepts introduced:**
- Controlled input — `value={title}` + `onChange={e => setTitle(e.target.value)}`; React owns the value, not the browser
- `e.preventDefault()` — stops browser default form behaviour (page reload); we take over
- Props — values passed from parent to child component; like function arguments (`onBookAdded`)
- Callback prop pattern — parent passes a function down; child calls it after save to trigger parent action
- `refreshKey` pattern — incrementing a `key` prop on `<BookList>` forces React to remount it, re-running `useEffect` and re-fetching
- `JSON.stringify({...})` — converts JS object to JSON string for the request body
- `parseInt()` — converts string input value to integer for `pageCount`

**Files created/changed:**
| File | Action |
|------|--------|
| `frontend/src/AddBook.jsx` | Created — 3 controlled inputs, `handleSubmit` calls POST /books, clears form, calls `onBookAdded()` |
| `frontend/src/App.jsx` | Updated — adds `refreshKey` state, passes `onBookAdded` callback to `<AddBook>`, `key={refreshKey}` on `<BookList>` |

**Output observed:** Added new book via form → appeared in list immediately without page reload ✅

**Deep-dive discussed:**
- Full request journey traced: click → fetch() POST → @RequestBody → BookService → JpaRepository.save() → Hibernate 2 INSERTs → PostgreSQL 2 rows
- Queried PostgreSQL directly via `docker exec psql` — confirmed JOINED strategy writes separate rows in `abstract_library_item` and `book`, linked by same `id`
- `@Table(name = "...")` annotation is where table names are defined in code; Hibernate snake_cases field names to column names (`pageCount` → `page_count`)

---

## Phase 4 Complete ✅

All 4 steps done:
- Vite React app scaffolded in `frontend/` ✅
- `BookList` component fetches and displays books from backend ✅
- `AddBook` form sends POST to backend and refreshes list ✅
- Full browser → Spring Boot → Hibernate → PostgreSQL → browser loop working ✅

**Next: Phase 5 — Logging & Monitoring**

---

## 2026-08-04

### Phase 5, Step 1 — SLF4J Logging ✅

**Concept taught:** Logging is the app's black box — how it records its own story for ops teams, debugging, and incident replay. SLF4J is the logging interface standard; Logback (included by Spring Boot) is the backend that writes the lines.

**Analogy used:** Black box recorder on a plane — even after a crash you can replay exactly what happened. SLF4J = power socket standard; code plugs into the interface, the actual electricity (Logback) can be swapped without rewiring.

**New concepts introduced:**
- SLF4J — logging interface; already on classpath via Spring Boot, no new dependency needed
- `LoggerFactory.getLogger(ClassName.class)` — one logger per class, tagged with the class name
- `static final Logger log` — declared once per class, shared across all instances
- Log levels: TRACE → DEBUG → INFO → WARN → ERROR (low → high severity)
- `{}` placeholders — SLF4J fills them in lazily; no string concatenation unless the log level is active
- `logging.level.com.library=DEBUG` in `application.properties` — turns on DEBUG for our package only; Spring internals stay at INFO

**Files changed:**
| File | Change |
|------|--------|
| `service/BookService.java` | Added logger; `INFO` on addBook (title/author/pageCount + saved id), `DEBUG` on getAllBooks (count), `WARN` on book not found |
| `api/BookController.java` | Added logger; `INFO` on every GET and POST request |
| `src/main/resources/application.properties` | Added `logging.level.com.library=DEBUG` |

**Output observed:**
```
INFO  c.l.api.BookController    : POST /books - title='Dune'
INFO  c.l.service.BookService   : Adding book: title='Dune', author='Frank Herbert', pageCount=412
INFO  c.l.service.BookService   : Book saved with id=5
INFO  c.l.api.BookController    : GET /books
DEBUG c.l.service.BookService   : getAllBooks() returned 5 book(s)
```

**Key takeaway:** Every API call now leaves a trace in the terminal. DEBUG is hidden by default and turned on per-package in `application.properties` — no code changes needed to toggle verbosity.

---

### Phase 5, Step 2 — Spring Actuator ✅

**Concept taught:** Production systems need a standard way to ask "is this app healthy?" — used by load balancers, Kubernetes probes, and ops teams. Spring Actuator adds built-in HTTP health/metrics endpoints with one dependency.

**Analogy used:** A doctor's check-up — standard questions, standard vitals. Actuator is the app's vitals panel.

**New concepts introduced:**
- `spring-boot-starter-actuator` — one dependency; Spring wires all endpoints automatically, no code to write
- `GET /actuator/health` — standard health check; returns `{"status":"UP"}` + component breakdown
- `GET /actuator/metrics` — JVM stats, memory, request counts
- `management.endpoints.web.exposure.include` — controls which endpoints are exposed over HTTP
- `management.endpoint.health.show-details=always` — shows per-component health (db, disk, SSL, etc.)
- `livenessState` / `readinessState` — Kubernetes liveness + readiness probe endpoints (preview of Phase 9)

**Files changed:**
| File | Change |
|------|--------|
| `pom.xml` | Added `spring-boot-starter-actuator` dependency |
| `src/main/resources/application.properties` | Added `management.endpoints.web.exposure.include=health,info,metrics` and `show-details=always` |

**Output observed:**
```
db              UP   ← PostgreSQL connection verified
diskSpace       UP
livenessState   UP   ← K8s liveness probe (Phase 9)
ping            UP
readinessState  UP   ← K8s readiness probe (Phase 9)
ssl             UP
```

**Key takeaway:** One dependency gives the app a full health panel. `db: UP` means Spring tested the PostgreSQL connection on startup. `livenessState` and `readinessState` are the exact endpoints Kubernetes will poll in Phase 9 to manage pod lifecycle.

---

## Phase 5 Complete ✅

Both steps done:
- SLF4J logging in BookService + BookController — every request leaves a trace ✅
- Spring Actuator — `/actuator/health` shows all components UP including PostgreSQL ✅

**Next: Phase 6 — Build Tooling**

---

## 2026-08-04

### Phase 6, Step 1 — pom.xml Line by Line ✅

**Concept taught:** `pom.xml` is the Maven blueprint — it describes who the project is, what it depends on, and what Maven can do with it. Every section has a specific purpose.

**Analogy used:** Building a house — order materials (dependencies), follow assembly sequence (lifecycle), know who the contractor is (plugins). Parent POM = company policy handbook; child inherits all rules and only overrides what it needs.

**Sections covered:**
- Header — boilerplate XML namespace, never changes
- `<parent>` — `spring-boot-starter-parent` supplies compatible versions for all Spring libraries; child dependencies need no version numbers
- `<groupId>/<artifactId>/<version>` — project identity; output JAR named `library-app-0.0.1-SNAPSHOT.jar`; SNAPSHOT = work in progress
- `<properties>` — reusable values; `java.version=24` tells compiler plugin which Java to target
- `<dependencies>` — what to download from Maven Central; each has groupId + artifactId + optional scope
- `<scope>runtime</scope>` — PostgreSQL driver never imported in code; Hibernate loads it by reflection at runtime
- `<scope>test</scope>` — JUnit/Mockito only on classpath during `mvn test`; not bundled in production JAR
- `<build><plugins>` — `spring-boot-maven-plugin` adds `mvn spring-boot:run` and fat JAR repackaging

---

### Phase 6, Step 2 — Maven Lifecycle ✅

**Concept taught:** Maven runs a fixed sequence of phases. Asking for phase N automatically runs phases 1–N first. Each phase has one job.

**Analogy used:** Car assembly line — paint before frame is welded = impossible. Order is fixed, each step depends on the previous.

**Phases covered:**
| Phase | Command | What it does |
|---|---|---|
| compile | `mvn compile` | `.java` → `.class` bytecode in `target/classes/` |
| test | `mvn test` | Runs JUnit tests in `src/test/` |
| package | `mvn package` | Bundles into fat JAR in `target/` |
| clean | `mvn clean` | Deletes `target/` — start fresh |

**Output observed:**
```
target/library-app-0.0.1-SNAPSHOT.jar  54 MB
```
54 MB = source code (a few KB) + all bundled dependencies (Spring, Tomcat, Hibernate, Jackson, PostgreSQL driver). Self-contained — runs with `java -jar` anywhere Java is installed. No Maven needed at runtime.

**Key takeaway:** `mvn clean package` = wipe stale output + compile + test + bundle. Standard command before shipping or building a Docker image.

---

### Phase 6, Step 3 — package.json for the Frontend ✅

**Concept taught:** `package.json` is `pom.xml` for JavaScript. Same ideas, different ecosystem.

**Side-by-side comparison:**
| Maven | npm | Purpose |
|---|---|---|
| `pom.xml` | `package.json` | Project descriptor |
| Maven Central | npmjs.com | Package registry |
| `~/.m2/` | `node_modules/` | Local cache |
| `mvn package` | `npm run build` | Production bundle |
| `target/*.jar` | `dist/` | Production output |

**Sections covered:**
- `name/private/version/type` — project identity; `private:true` = never publish to npmjs; `type:module` = ES module syntax
- `scripts` — shortcuts: `npm run dev` → `vite`, `npm run build` → `vite build`
- `dependencies` — React ships to the browser (production)
- `devDependencies` — Vite, oxlint are build tools; never in the browser
- `^19.2.8` — caret range: "this version or newer minor/patch, not next major"
- `package-lock.json` — exact locked versions + integrity hashes; always commit to git; equivalent of Maven's resolved dependency tree

**Output observed:**
```
dist/index.html                    0.45 kB
dist/assets/index-DRqs47u-.js    191.94 kB   ← all JSX + React bundled + minified
dist/assets/index-nqMpL4T3.css     1.78 kB
```
All `.jsx` files + React library → 3 files the browser can load directly. Hash in filename busts browser cache on each build.

**Conceptual discussion — history of frontend tooling:**
- 2005–2012: hand-written HTML/CSS/JS + jQuery for DOM manipulation
- 2013: React introduced — UI as pure reflection of data; JSX; eliminated manual DOM sync
- 2016: Create React App hid Webpack/Babel config (was half a day to bootstrap)
- 2020: Vite replaced CRA — same scaffold in seconds
- `node_modules/` has always been a black box developers trust without reading

---

## Phase 6 Complete ✅

All 3 steps done:
- `pom.xml` every section understood ✅
- Maven lifecycle: compile → package → clean ✅
- `package.json`, scripts, dependencies vs devDependencies, `npm run build` → `dist/` ✅

**Next: Phase 7 — Testing**

---

## 2026-08-04

### Phase 7, Step 1 — Unit Tests (JUnit + Mockito) ✅

**Concept taught:** Automated tests check that code behaves correctly without running the full app. Unit tests isolate one class — fake its dependencies with Mockito so no DB or server is needed.

**Analogy used:** Bridge load checklist — every time you add a beam, automated tests verify nothing broke. Chef analogy for Mockito: testing the chef (BookService) with a fake pantry (mock repository) that records what ingredients were requested.

**New concepts introduced:**
- `src/test/java/` — mirror of `src/main/java/`; Maven runs everything here during `mvn test`
- `@Test` — marks a method as a test case; JUnit runs it
- `@BeforeEach` — runs before every test; creates fresh mocks so tests don't share state
- `Mockito.mock(Class)` — creates a fake object that records calls but does nothing by default
- `when(...).thenReturn(...)` — instructs mock: "when this method is called, return this value"
- `verify(mock, times(1)).method()` — asserts the method was called exactly once
- `ArgumentCaptor` — intercepts the argument passed to a mock method so you can inspect it
- `assertEquals(expected, actual)` — fails if values don't match
- `assertTrue(condition)` — fails if condition is false
- Arrange → Act → Assert — the pattern every test follows

**Root cause / fix:** `spring-boot-starter-test` was not in `pom.xml` — JUnit/Mockito not on classpath. Added with `<scope>test</scope>`.

**Files created:**
| File | Purpose |
|------|---------|
| `src/test/java/com/library/service/BookServiceTest.java` | 4 unit tests: addBook saves correct fields, getAllBooks returns list, getBookSummary found, getBookSummary not found |

**Output observed:**
```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.625 s
BUILD SUCCESS
```
No Docker, no Spring Boot, no database — 4 tests in 0.6 seconds.

---

### Phase 7, Step 1b — JaCoCo Coverage Report ✅

**Concept taught:** Coverage measures what percentage of code was executed when tests ran. Three types: line (was this line run?), branch (was every if/else path taken?), method (was this method called?).

**Analogy used:** Car inspection checklist — coverage asks which parts of the car your checklist actually checked.

**New concepts introduced:**
- JaCoCo — Java Code Coverage; plugs into Maven; generates HTML report in `target/site/jacoco/`
- Green lines — executed; Red lines — never executed; Yellow lines — partially covered (one branch only)
- Industry targets: 70–80% for most companies, 90%+ for safety-critical; 100% coverage ≠ zero bugs
- Coverage = floor, not ceiling — you can execute every line without asserting the right outcome

**Files changed:**
| File | Change |
|------|--------|
| `pom.xml` | Added `jacoco-maven-plugin` with `prepare-agent` + `report` executions |

**Output observed:** HTML report at `target/site/jacoco/index.html` — BookService well covered (green), BookController/models red (not tested yet).

**Key takeaway:** `mvn test` now automatically generates a coverage report. BookService is well-tested; controller and models will be covered in Steps 2–3.

---

**Next: Phase 7, Step 2 — Controller Tests (Integration)**

---

## 2026-08-06

### Phase 7, Step 2 — Integration Tests (MockMvc) ✅

**Concept taught:** Integration tests test the HTTP layer — routing, JSON serialisation, status codes — without starting a real server. MockMvc is Spring's in-memory fake HTTP client. The only thing mocked is the service; the real controller and HTTP infrastructure are used.

**Integration test vs unit test:**
| | Unit test | Integration test (MockMvc) |
|---|---|---|
| What's real? | BookService logic | HTTP routing + JSON + status codes |
| What's mocked? | BookRepository | BookService |
| Server needed? | No | No |
| Network port? | No | No |
| Speed | ~0.6s | ~0.8s |

**Spring Boot 4.0 breaking change — @WebMvcTest removed:**
Spring Boot 4.0 removed `@WebMvcTest` and `@MockBean` entirely. The spring-boot-test-autoconfigure 4.0.7 jar only contains jdbc and json test slices — no web/servlet slice. The replacement is `MockMvcBuilders.standaloneSetup(controller).build()` from `org.springframework.test.web.servlet.setup` (Spring Framework 7, always on the test classpath via spring-test).

**New concepts introduced:**
- `MockMvc` — Spring's in-memory fake HTTP client; no TCP port, no real server
- `MockMvcBuilders.standaloneSetup(controller)` — builds MockMvc around one controller instance; no Spring context needed
- `mvc.perform(get("/books"))` — sends a fake GET request
- `.andExpect(status().isOk())` — asserts HTTP 200
- `.andExpect(jsonPath("$[0].title").value("Dune"))` — asserts a field in the JSON response body
- jsonPath syntax: `$` = root, `[0]` = first element, `.title` = field
- `doNothing().when(mock).method()` — explicit Mockito setup for void methods

**Files created:**
| File | Purpose |
|------|---------|
| `src/test/java/com/library/api/BookControllerTest.java` | 2 integration tests: GET /books returns 200 + JSON array; POST /books returns 201 |

**Test suite state after this step:**
| Test class | Tests | What's mocked |
|---|---|---|
| BookServiceTest | 4 | BookRepository |
| BookControllerTest | 2 | BookService |

**Output observed:**
```
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**Error hit and fixed:**
- First attempt used `@WebMvcTest` and `@MockBean` — both removed in Spring Boot 4.0
- Error: `package org.springframework.boot.test.autoconfigure.web.servlet does not exist`
- Fix: rewrote using `MockMvcBuilders.standaloneSetup(controller)` + plain `Mockito.mock()`

---

### Phase 7, Step 3 — Regression via Refactor ✅

**Concept taught:** A regression is a bug introduced by a code change that breaks previously working functionality. Tests catch regressions at the moment of change — before shipping.

**Analogy used:** Bridge beam checklist — every time you move a beam, the checklist re-verifies the whole structure. One change in `BookService.addBook()` and the test immediately flags the breakage.

**The demo (deliberate bug):**
1. Changed `new Book(title, author, pageCount)` → `new Book(title, author, 0)` in `BookService.addBook()`
2. Ran `mvn test`
3. Test `addBook_savesBookWithCorrectFields` failed instantly:
   ```
   org.opentest4j.AssertionFailedError: expected: <412> but was: <0>
       at BookServiceTest.addBook_savesBookWithCorrectFields(BookServiceTest.java:37)
   ```
4. Restored `new Book(title, author, pageCount)` — all 6 tests green again

**What made the test catch it:**
The unit test from Step 1 captures the exact `Book` object passed to `repository.save()` via `ArgumentCaptor` and asserts all three fields (title, author, pageCount). Hardcoding `0` for pageCount failed the assertion with exact file + line.

**Key takeaway:** You can refactor with confidence. Change anything you like — if you break existing behaviour, the build fails before the code reaches anyone else. No manual testing needed for regressions.

**Final test suite:**
```
mvn test → Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```
