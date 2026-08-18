# Reference Handbook — Library App

A complete reference covering every concept, annotation, pattern, and decision from this project. Written for interview prep and future projects.

---

## Table of Contents

1. [Java & OOP](#1-java--oop)
2. [Spring Boot](#2-spring-boot)
3. [React & JavaScript](#3-react--javascript)
4. [Build Tooling](#4-build-tooling)
5. [Database Layer (JPA + PostgreSQL)](#5-database-layer-jpa--postgresql)
6. [Testing](#6-testing)
7. [Logging & Monitoring](#7-logging--monitoring)
8. [Docker & Containerisation](#8-docker--containerisation)
9. [Kubernetes](#9-kubernetes)
10. [GitOps with ArgoCD](#10-gitops-with-argocd)
11. [Full Architecture Diagram](#11-full-architecture-diagram)

---

## 1. Java & OOP

### Encapsulation

Hide internal data behind `private` fields. Expose only through controlled getters/setters.

```java
public class Book {
    private String title;       // private — no one can change it directly
    private int pageCount;

    public String getTitle() { return title; }

    public void setPageCount(int pageCount) {
        if (pageCount > 0) {    // setter has a guard — invalid values rejected
            this.pageCount = pageCount;
        }
    }
}
```

**Why it matters:** Callers can't put the object in an invalid state. You can change the internal implementation without breaking callers.

---

### Abstraction

Define a contract (what something does) separately from the implementation (how it does it).

**Interface** — pure contract, no implementation:
```java
public interface LibraryItem {
    String getSummary();     // every item must implement this
    boolean isAvailable();
}
```

**Abstract class** — partial implementation shared by all subclasses:
```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractLibraryItem implements LibraryItem {
    @Id @GeneratedValue
    private Long id;
    private String title;
    private boolean available = true;

    // getSummary() NOT implemented here — subclasses must provide it
}
```

**Rule of thumb:** Interface = "can do" contract (e.g. `Printable`, `Serializable`). Abstract class = shared "is a" base (e.g. `AbstractLibraryItem`).

---

### Inheritance

A child class extends a parent, reusing shared fields and methods while adding its own.

```java
public class Book extends AbstractLibraryItem {
    private String author;
    private int pageCount;

    @Override
    public String getSummary() {
        return "\"" + getTitle() + "\" by " + author + " (" + pageCount + " pages)";
    }
}

public class Magazine extends AbstractLibraryItem {
    private String publisher;
    private int issueNumber;

    @Override
    public String getSummary() {
        return getTitle() + " — Issue #" + issueNumber + " by " + publisher;
    }
}
```

`Book` and `Magazine` both inherit `id`, `title`, `available` from `AbstractLibraryItem`.

---

### Polymorphism

One reference type, many underlying implementations. The correct method is called at runtime based on the actual object.

```java
List<LibraryItem> items = new ArrayList<>();
items.add(new Book("Dune", "Herbert", 412));
items.add(new Magazine("Nature", "Springer", 101));

for (LibraryItem item : items) {
    System.out.println(item.getSummary());  // calls Book.getSummary() or Magazine.getSummary()
                                            // decided at runtime, not compile time
}
```

**Why it matters:** Code that works with `LibraryItem` doesn't need to change when you add new types (DVD, AudioBook, etc.).

---

### Three-Layer Architecture

| Layer | Package | Role |
|---|---|---|
| API | `com.library.api` | Receives HTTP requests, returns responses |
| Service | `com.library.service` | Business logic — validates, transforms, orchestrates |
| Repository | `com.library.repository` | Data access only — talks to the database |

Each layer talks only to its immediate neighbour. The API never touches the database directly.

---

## 2. Spring Boot

### What Spring Boot Does

Spring Boot is a pre-configured wrapper around the Spring Framework. It:
- Starts an embedded Tomcat server (no separate server install)
- Auto-detects your dependencies and configures them
- Scans packages for `@Component`, `@Service`, `@Repository`, `@RestController` and wires them together

**Entry point:**
```java
@SpringBootApplication   // = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
```

---

### Dependency Injection

Spring creates and manages object instances ("beans"). You declare what you need; Spring delivers it.

```java
@Service
public class BookService {
    private final BookRepository repository;

    @Autowired                          // Spring injects BookRepository here
    public BookService(BookRepository repository) {
        this.repository = repository;
    }
}
```

**Constructor injection** (used here) is preferred over field injection — it makes dependencies explicit and testable.

---

### REST Controller Annotations

```java
@CrossOrigin("*")                    // allow all origins (browser CORS)
@RestController                      // = @Controller + @ResponseBody; return values become JSON
@RequestMapping("/books")            // base path for all routes in this class
public class BookController {

    @GetMapping                      // handles GET /books
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(service.getAllBooks());   // 200 OK + JSON body
    }

    @PostMapping                     // handles POST /books
    public ResponseEntity<Void> addBook(@RequestBody BookRequest request) {
        // @RequestBody: Spring reads the JSON body and deserialises it into BookRequest
        service.addBook(request.getTitle(), request.getAuthor(), request.getPageCount());
        return ResponseEntity.status(201).build();        // 201 Created, no body
    }
}
```

| Annotation | Purpose |
|---|---|
| `@RestController` | All methods return data (JSON), not view names |
| `@RequestMapping("/books")` | Prefix for all routes |
| `@GetMapping` | Maps `GET /books` |
| `@PostMapping` | Maps `POST /books` |
| `@RequestBody` | Reads and deserialises JSON from the HTTP request body |
| `@CrossOrigin` | Allows browser requests from different origins |
| `ResponseEntity<T>` | Wraps return value with an explicit HTTP status code |

---

### DTO (Data Transfer Object)

A plain class whose only job is to carry data in and out of the API. Never extends anything, has no business logic.

```java
public class BookRequest {
    private String title;
    private String author;
    private int pageCount;
    // getters + setters (Jackson needs these for deserialisation)
}
```

**Why not use `Book` directly?** `Book` extends `AbstractLibraryItem` and has extra complexity. The DTO exposes only what the caller needs to provide.

---

### application.properties

Runtime configuration — keeps connection details and settings out of code.

```properties
# PostgreSQL connection (reads env vars with fallbacks for local dev)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/librarydb}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:secret}

# JPA settings
spring.jpa.hibernate.ddl-auto=update   # auto-create/update tables (dev only)
spring.jpa.show-sql=true               # print generated SQL to console

# Logging
logging.level.com.library=DEBUG        # DEBUG for our code, INFO for everything else

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

`${DB_URL:fallback}` means: use the `DB_URL` environment variable if set, otherwise use the fallback. This makes the same config work locally and in Docker/K8s.

---

## 3. React & JavaScript

### Component

A React component is a JavaScript function that returns JSX (HTML-like markup with embedded expressions).

```jsx
function BookList() {
    const [books, setBooks] = useState([])   // declare state

    useEffect(() => {
        fetch('/api/books')
            .then(res => res.json())
            .then(data => setBooks(data))
    }, [])                                   // [] = run once on mount

    return (
        <ul>
            {books.map(book => (
                <li key={book.id}>{book.title} by {book.author}</li>
            ))}
        </ul>
    )
}
```

---

### useState

Declares a piece of state. React re-renders the component whenever the setter is called.

```jsx
const [title, setTitle] = useState('')   // initial value = ''

// Later:
setTitle('Dune')   // triggers re-render; component sees title === 'Dune'
```

---

### useEffect

Runs code after the component renders. The second argument controls when it runs.

```jsx
useEffect(() => { ... }, [])          // once on mount (like componentDidMount)
useEffect(() => { ... }, [bookId])    // whenever bookId changes
useEffect(() => { ... })              // after every render (use rarely)
```

---

### Controlled Input

React owns the input value via state. The input shows exactly what's in state; state updates on every keystroke.

```jsx
<input
    value={title}
    onChange={e => setTitle(e.target.value)}
    required
/>
```

**Why:** Gives React full control. You can validate, transform, or clear the value programmatically.

---

### Props and Callback Pattern

Props pass values from parent to child. Callback props pass functions down so the child can trigger parent actions.

```jsx
// Parent
function App() {
    const [refreshKey, setRefreshKey] = useState(0)

    return (
        <>
            <AddBook onBookAdded={() => setRefreshKey(k => k + 1)} />
            <BookList key={refreshKey} />   {/* key change forces remount = re-fetch */}
        </>
    )
}

// Child
function AddBook({ onBookAdded }) {
    function handleSubmit(e) {
        e.preventDefault()
        fetch('/api/books', { method: 'POST', ... })
            .then(() => onBookAdded())   // tell parent: book was added
    }
}
```

---

### fetch() API

Browser built-in for HTTP requests. Returns a Promise.

```javascript
// GET
fetch('/api/books')
    .then(res => res.json())
    .then(data => setBooks(data))

// POST
fetch('/api/books', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, author, pageCount: parseInt(pageCount) })
})
```

---

## 4. Build Tooling

### Maven (pom.xml)

```xml
<parent>
    <!-- Inherits compatible versions for all Spring libraries -->
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.7</version>
</parent>

<groupId>com.library</groupId>
<artifactId>library-app</artifactId>
<version>0.0.1-SNAPSHOT</version>

<properties>
    <java.version>24</java.version>  <!-- tells compiler which Java to target -->
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- no <version> — inherited from parent -->
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>   <!-- not imported in code; Hibernate loads it by reflection -->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>      <!-- only on classpath during mvn test; not in production JAR -->
    </dependency>
</dependencies>
```

### Maven Lifecycle

| Command | Runs phases | What happens |
|---|---|---|
| `mvn compile` | compile | `.java` → `.class` in `target/classes/` |
| `mvn test` | compile → test | Compiles + runs JUnit tests |
| `mvn package` | compile → test → package | Compiles + tests + bundles fat JAR |
| `mvn clean` | clean | Deletes `target/` |
| `mvn clean package` | clean → … → package | Fresh full build |
| `mvn spring-boot:run` | (compile + start) | Starts app without packaging |

**Fat JAR:** `target/library-app-0.0.1-SNAPSHOT.jar` (~54 MB) = your code + all dependencies bundled. Runs with `java -jar` anywhere Java is installed. No Maven needed at runtime.

---

### npm (package.json)

```json
{
  "scripts": {
    "dev": "vite",           // npm run dev  → starts hot-reload dev server on :5173
    "build": "vite build",   // npm run build → bundles to dist/
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^19.2.8"       // ships to the browser
  },
  "devDependencies": {
    "vite": "^8.2.0"         // build tool only — never in the browser
  }
}
```

**`^` version range:** "19.2.8 or any higher minor/patch, but not 20.x". Allows `npm update` to pull security patches without breaking changes.

**`package-lock.json`:** Locks exact versions + integrity hashes for every transitive dependency. Always commit to git. Ensures every developer and CI build gets identical packages.

| Maven | npm |
|---|---|
| `pom.xml` | `package.json` |
| `target/*.jar` | `dist/` |
| Maven Central | npmjs.com |
| `~/.m2/` | `node_modules/` |
| `<scope>test</scope>` | `devDependencies` |

---

## 5. Database Layer (JPA + PostgreSQL)

### JPA Annotations

```java
@Entity                                          // marks class as a database table
@Inheritance(strategy = InheritanceType.JOINED)  // parent table + child table joined by FK
@Table(name = "abstract_library_item")           // explicit table name
public abstract class AbstractLibraryItem {

    @Id                                          // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // DB auto-assigns IDs
    private Long id;

    private String title;
    private boolean available = true;

    protected AbstractLibraryItem() {}           // required by JPA (reflection-based instantiation)
}

@Entity
@Table(name = "book")
public class Book extends AbstractLibraryItem {
    private String author;

    @Column(name = "page_count")                 // explicit column name
    private int pageCount;
}
```

---

### JOINED Inheritance Strategy

Three inheritance strategies — JOINED was chosen:

| Strategy | Tables | Pros | Cons |
|---|---|---|---|
| SINGLE_TABLE | 1 | Fastest queries | NULLs everywhere, can't enforce NOT NULL |
| TABLE_PER_CLASS | One per concrete type | No NULLs | Shared fields duplicated, UNION queries |
| **JOINED** (chosen) | 1 parent + 1 per child | Normalised, no NULLs, scales cleanly | JOIN on every read |

SQL Hibernate generates for a Book query:
```sql
SELECT b.id, a.title, a.available, b.author, b.page_count
FROM book b
JOIN abstract_library_item a ON b.id = a.id
```

---

### JpaRepository

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);   // derived query — Spring generates the SQL
}
```

`JpaRepository<Book, Long>` gives you for free:
- `save(book)` — INSERT or UPDATE
- `findAll()` — SELECT all
- `findById(id)` — SELECT by PK
- `deleteById(id)` — DELETE
- `count()` — COUNT(*)

**Derived queries:** Spring reads the method name and generates SQL. `findByTitle` → `WHERE title = ?`. `findByAuthorAndPageCountGreaterThan` → `WHERE author = ? AND page_count > ?`.

---

### application.properties — DDL Auto

| Value | Behaviour | When to use |
|---|---|---|
| `create` | Drop and recreate all tables on startup | Never in production |
| `update` | Add missing columns/tables, never drop | Dev/learning |
| `validate` | Check schema matches entities; fail if not | Staging |
| `none` | Do nothing | Production (use Flyway/Liquibase) |

---

## 6. Testing

### Unit Tests (JUnit 5 + Mockito)

Test one class in isolation. Fake all dependencies with mocks.

```java
class BookServiceTest {
    private BookRepository repository;
    private BookService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BookRepository.class);   // fake repository
        service = new BookService(repository);
    }

    @Test
    void addBook_savesBookWithCorrectFields() {
        // Act
        service.addBook("Dune", "Herbert", 412);

        // Assert — capture what was passed to repository.save()
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(repository, times(1)).save(captor.capture());
        assertEquals("Dune", captor.getValue().getTitle());
        assertEquals(412, captor.getValue().getPageCount());
    }
}
```

| Mockito method | Purpose |
|---|---|
| `Mockito.mock(Class)` | Creates a fake object |
| `when(mock.method()).thenReturn(value)` | Programs the fake's behaviour |
| `verify(mock, times(n)).method()` | Asserts the method was called n times |
| `ArgumentCaptor` | Intercepts the argument passed to a mock to inspect it |

---

### Integration Tests (MockMvc)

Test the HTTP layer — routing, JSON, status codes. No real server started.

```java
class BookControllerTest {
    private MockMvc mvc;
    private BookService service;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(BookService.class);
        mvc = MockMvcBuilders.standaloneSetup(new BookController(service)).build();
    }

    @Test
    void getAllBooks_returns200AndJsonArray() throws Exception {
        when(service.getAllBooks()).thenReturn(List.of(new Book("Dune", "Herbert", 412)));

        mvc.perform(get("/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Dune"));
    }
}
```

**Note:** Spring Boot 4.0 removed `@WebMvcTest` and `@MockBean`. Use `MockMvcBuilders.standaloneSetup()` + plain `Mockito.mock()` instead.

---

### JaCoCo Coverage

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution><goals><goal>prepare-agent</goal></goals></execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

`mvn test` → HTML report at `target/site/jacoco/index.html`

- Green = executed, Red = never executed, Yellow = partial branch coverage
- Industry target: 70–80% for most teams

---

## 7. Logging & Monitoring

### SLF4J Logging

```java
private static final Logger log = LoggerFactory.getLogger(BookService.class);

log.info("Adding book: title='{}', author='{}'", title, author);   // INFO level
log.debug("getAllBooks() returned {} book(s)", books.size());       // DEBUG level
log.warn("Book not found: {}", title);                              // WARN level
log.error("Unexpected error", exception);                           // ERROR level
```

`{}` placeholders — SLF4J fills them in lazily. No string concatenation if the level is inactive.

**Levels (low → high):** TRACE → DEBUG → INFO → WARN → ERROR

`logging.level.com.library=DEBUG` in `application.properties` — DEBUG for our code, INFO for Spring internals.

---

### Spring Actuator

```
GET /actuator/health
{
  "status": "UP",
  "components": {
    "db":             { "status": "UP" },       ← PostgreSQL connection verified
    "diskSpace":      { "status": "UP" },
    "livenessState":  { "status": "UP" },       ← K8s liveness probe
    "readinessState": { "status": "UP" }        ← K8s readiness probe
  }
}
```

`livenessState` and `readinessState` are the exact endpoints Kubernetes polls for pod lifecycle management.

---

## 8. Docker & Containerisation

### Core Concepts

| Concept | Analogy | Description |
|---|---|---|
| Image | Cookie cutter | Read-only blueprint: base OS + runtime + your app |
| Container | Cookie | A running instance of an image — isolated process |
| Layer | Lasagne layer | Each Dockerfile instruction = one cached layer |
| Registry | Maven Central | Stores and distributes images (Docker Hub, etc.) |

---

### Backend Dockerfile (Multi-Stage)

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
RUN ./mvnw dependency:go-offline -q    # download deps (cached layer)
COPY src ./src
RUN ./mvnw package -DskipTests -q      # compile + package JAR

# Stage 2: Run (lean image — JRE only, no compiler)
FROM eclipse-temurin:24-jre AS runner
WORKDIR /app
COPY --from=builder /app/target/library-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

**Layer order principle:** Copy `pom.xml` + download deps before copying source. If only source changes, Maven download layer is reused from cache.

---

### Frontend Dockerfile (Multi-Stage)

```dockerfile
# Stage 1: Build React app
FROM node:24-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --silent                    # exact install from lock file
COPY index.html . 
COPY src ./src
COPY vite.config.js .
RUN npm run build                      # → dist/

# Stage 2: Serve with Nginx
FROM nginx:alpine AS runner
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

### Nginx Reverse Proxy Config

```nginx
server {
    listen 80;

    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;   # SPA routing — unknown paths serve index.html
    }

    location /api/ {
        proxy_pass http://backend:8080/;    # strip /api/ prefix, forward to backend service
        proxy_set_header Host $host;
    }
}
```

Why needed in K8s: the browser calls the frontend Nginx at the same origin. Nginx proxies `/api/` to the backend Service internally. The browser never talks to the backend directly.

---

### Docker Compose

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: librarydb
    volumes:
      - postgres-data:/var/lib/postgresql/data   # named volume = data survives restart
    ports:
      - "5432:5432"

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/librarydb   # service name as hostname
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres-data:   # Docker manages the actual storage location
```

**Key commands:**
```bash
docker compose up --build    # build images + start all containers
docker compose down          # stop containers (data preserved)
docker compose down -v       # stop containers + delete volumes (data gone)
```

---

## 9. Kubernetes

### Core Resources

| Resource | Analogy | Purpose |
|---|---|---|
| Pod | One hotel room | Smallest unit — wraps one container |
| Deployment | Booking policy | Keeps N pods running; restarts crashed ones |
| StatefulSet | Reserved suite with name | Like Deployment but pods have stable names + own storage |
| Service (ClusterIP) | Internal phone extension | Stable DNS name for pods, reachable only inside cluster |
| Service (NodePort) | Lobby phone number | Exposes service on a port of every node — reachable externally |
| ConfigMap | Notice board | Non-sensitive config as key/value pairs |
| Secret | Locked envelope | Sensitive data (passwords) — base64-encoded |
| PVC | Storage reservation | Requests a piece of persistent storage |
| PV | Actual storage | Fulfils a PVC claim (auto-provisioned in Minikube) |

---

### Deployment Manifest

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: backend        # links Deployment to its pods
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
        - name: backend
          image: library-backend:latest
          imagePullPolicy: Never    # use local image, don't pull from Docker Hub
          ports:
            - containerPort: 8080
          env:
            - name: DB_URL
              valueFrom:
                configMapKeyRef:   # read from ConfigMap
                  name: library-config
                  key: DB_URL
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:      # read from Secret
                  name: library-secret
                  key: DB_PASSWORD
```

---

### StatefulSet vs Deployment

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
spec:
  serviceName: postgres       # must match the headless Service name
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    ...
  volumeClaimTemplates:       # automatically creates one PVC per pod
    - metadata:
        name: postgres-data
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 1Gi
```

StatefulSet pod names: `postgres-0`, `postgres-1` (stable, not random)
PVC name: `postgres-data-postgres-0` = `{template-name}-{statefulset-name}-{pod-index}`

---

### ConfigMap + Secret

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: library-config
data:
  DB_URL: jdbc:postgresql://postgres:5432/librarydb
  DB_USER: postgres
  DB_NAME: librarydb
```

```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: library-secret
type: Opaque
data:
  DB_PASSWORD: c2VjcmV0    # base64 of "secret" — echo -n "secret" | base64
```

⚠️ base64 is encoding, not encryption. Anyone can decode it. In production: use Vault, Sealed Secrets, or K8s encryption at rest.

---

### Kustomize

```
k8s/
├── base/                        # one copy of every manifest
│   ├── kustomization.yaml       # lists all base resources
│   └── *.yaml
└── overlays/
    ├── dev/
    │   └── kustomization.yaml   # points at base, no changes
    └── prod/
        └── kustomization.yaml   # patches backend to 3 replicas
```

**Prod overlay example:**
```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
bases:
  - ../../base
patches:
  - patch: |-
      apiVersion: apps/v1
      kind: Deployment
      metadata:
        name: backend
      spec:
        replicas: 3
    target:
      kind: Deployment
      name: backend
```

**Commands:**
```bash
kubectl kustomize k8s/overlays/prod        # preview merged YAML
kubectl apply -k k8s/overlays/dev          # apply dev overlay
```

---

### Key kubectl Commands

```bash
kubectl get pods                           # list all pods in default namespace
kubectl get pods -n argocd                 # list pods in argocd namespace
kubectl get services                       # list services
kubectl get pvc                            # list PersistentVolumeClaims
kubectl logs deployment/backend            # view logs
kubectl logs deployment/backend --tail=20  # last 20 lines
kubectl describe pod <name>                # detailed pod info + events
kubectl exec deployment/frontend -- sh     # exec into a pod
kubectl apply -f manifest.yaml             # apply a manifest
kubectl delete pod -l app=backend          # delete pods by label (Deployment recreates them)
kubectl rollout restart deployment/backend # rolling restart (picks up new image)
minikube image build -t name:tag .         # build image inside Minikube's Docker
minikube service frontend --url            # get tunnelled URL for a NodePort service
```

---

## 10. GitOps with ArgoCD

### What GitOps Means

The git repository IS the desired state of the cluster. ArgoCD runs inside the cluster, watches git, and applies changes automatically. You never run `kubectl apply` to deploy.

| Traditional | GitOps |
|---|---|
| `kubectl apply -k overlays/dev` | `git push` |
| You push to the cluster | Cluster pulls from git |
| No audit trail | Every change = a git commit |
| Manual drift repair | ArgoCD self-heals drift |

---

### ArgoCD Application

```yaml
# ArgoCD Application resource
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: library-app
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.tools.sap/I348975/library-app
    targetRevision: master
    path: k8s/overlays/dev         # which Kustomize overlay to deploy
  destination:
    server: https://kubernetes.default.svc
    namespace: default
  syncPolicy:
    automated:
      prune: true                  # delete resources removed from git
      selfHeal: true               # revert manual kubectl changes
```

**To make a change:**
1. Edit a manifest (e.g. increase replicas in `k8s/overlays/dev/kustomization.yaml`)
2. `git push`
3. ArgoCD detects the new commit within ~3 minutes and applies it

**Status terms:**
- **Synced** — cluster matches git exactly
- **OutOfSync** — cluster differs from git (ArgoCD will fix it)
- **Healthy** — all pods are running and passing health checks

---

## 11. Full Architecture Diagram

### Local Dev (Docker Compose)

```
Your Browser (port 80)
    │
    ▼
┌─────────────────────────────────────────┐  docker-compose network
│  frontend container (Nginx, port 80)    │
│  GET /           → serves dist/         │
│  GET /api/books  → proxy_pass ──────────┼──► backend:8080
└─────────────────────────────────────────┘
                                          │
                              ┌───────────▼──────────────────┐
                              │  backend container           │
                              │  Spring Boot (port 8080)     │
                              │  BookController              │
                              │  BookService                 │
                              │  BookRepository              │
                              │  Hibernate / JPA             │
                              │  HikariCP → JDBC             │
                              └───────────┬──────────────────┘
                                          │
                              ┌───────────▼──────────────────┐
                              │  postgres container          │
                              │  PostgreSQL 16 (port 5432)   │
                              │  abstract_library_item table │
                              │  book table (JOINED FK)      │
                              └───────────┬──────────────────┘
                                          │
                              ┌───────────▼──────────────────┐
                              │  Named volume: postgres-data │
                              │  (survives container restart)│
                              └──────────────────────────────┘
```

---

### Kubernetes (Minikube)

```
Your Browser
    │  http://127.0.0.1:<tunnel-port>
    ▼
┌─────────────────────────────────────────────────────────────────┐
│  Minikube Node                                                  │
│                                                                 │
│  ┌─────────────────────────┐   ┌─────────────────────────┐     │
│  │  frontend Pod           │   │  backend Pod            │     │
│  │  Nginx                  │   │  Spring Boot            │     │
│  │  serves React at /      │   │  BookController         │     │
│  │  proxies /api/ → ───────┼──►│  BookService            │     │
│  └─────────────────────────┘   │  BookRepository         │     │
│             ▲                  │  Hibernate               │     │
│  Service: frontend (NodePort)  └──────────┬──────────────┘     │
│  port 30080 → pod 80           Service: backend (ClusterIP)     │
│                                port 8080                        │
│                                           │                     │
│                                ┌──────────▼──────────────┐     │
│                                │  postgres-0 Pod          │     │
│                                │  PostgreSQL 16           │     │
│                                └──────────┬──────────────┘     │
│                                Service: postgres (Headless)     │
│                                           │                     │
│                                ┌──────────▼──────────────┐     │
│                                │  PVC: postgres-data-     │     │
│                                │  postgres-0 (1Gi)        │     │
│                                │  Bound to PV (Minikube)  │     │
│                                └──────────────────────────┘     │
│                                                                 │
│  ConfigMap: library-config  (DB_URL, DB_USER, DB_NAME)         │
│  Secret:    library-secret  (DB_PASSWORD base64)               │
└─────────────────────────────────────────────────────────────────┘
        ▲
        │  ArgoCD watches git repo → auto-applies changes
        │
┌───────┴──────────────────────────────────────────────────┐
│  ArgoCD (namespace: argocd)                              │
│  Application: library-app                               │
│  Source: github.tools.sap/I348975/library-app           │
│  Path: k8s/overlays/dev                                 │
│  Sync: Automatic + Self Heal                            │
└──────────────────────────────────────────────────────────┘
        ▲
        │  git push
        │
┌───────┴──────────────────────────────────────────────────┐
│  Git (github.tools.sap + github.com/sampathshetty85)    │
│  k8s/base/*.yaml            ← source of truth           │
│  k8s/overlays/dev/          ← dev config (1 replica)    │
│  k8s/overlays/prod/         ← prod config (3 replicas)  │
└──────────────────────────────────────────────────────────┘
```

---

### Request Flow (full path for `POST /api/books`)

```
1. User fills form in browser → clicks "Add Book"
2. React handleSubmit() → fetch('/api/books', POST, JSON body)
3. Browser sends HTTP POST to http://127.0.0.1:<port>/api/books
4. Nginx (frontend pod) receives request
5. location /api/ matches → proxy_pass to http://backend:8080/books
6. K8s DNS resolves "backend" → backend ClusterIP (10.x.x.x)
7. Spring Boot BookController @PostMapping receives request
8. @RequestBody deserialises JSON → BookRequest object
9. BookController calls BookService.addBook(title, author, pageCount)
10. BookService creates new Book() → calls BookRepository.save(book)
11. Hibernate generates SQL:
      INSERT INTO abstract_library_item (title, available) VALUES (?, ?)
      INSERT INTO book (author, page_count, id) VALUES (?, ?, ?)
12. PostgreSQL writes 2 rows linked by the same id
13. Spring Boot returns HTTP 201 Created
14. Nginx passes 201 back to browser
15. React .then(() => onBookAdded()) → refreshKey++ → BookList remounts
16. BookList useEffect fires → fetch('/api/books', GET) → list refreshed
```

---

*Generated from the library-app teaching project — Phases 1–10 complete.*
