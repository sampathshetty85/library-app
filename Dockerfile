# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:24-jdk AS builder

WORKDIR /app

# Copy Maven wrapper first (changes rarely → cached layer)
COPY .mvn .mvn
COPY mvnw .

# Copy pom.xml and download dependencies (cached unless pom.xml changes)
COPY pom.xml .
RUN ./mvnw dependency:go-offline -q

# Copy source and build the fat JAR
COPY src ./src
RUN ./mvnw package -DskipTests -q

# ── Stage 2: Run ────────────────────────────────────────────────────────────
FROM eclipse-temurin:24-jre AS runner

WORKDIR /app

# Copy only the JAR from stage 1 — everything else is discarded
COPY --from=builder /app/target/library-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
