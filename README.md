# Kotlin JS on Node.js Experiment (Contacts)

This project is a Proof of Concept (PoC) to validate the technical viability and development experience of replacing **TypeScript/JavaScript** with **Kotlin Multiplatform (JS target)** in the **Node.js** ecosystem.

The goal is to explore how to integrate native Node.js libraries (such as Express and Mongoose) using Kotlin, leveraging its features like Coroutines and a robust Type System.

## 📋 Prerequisites

To run this project locally, you will need:

- **JDK 21** (Java Development Kit)
- **Node.js** (v22 or higher recommended for runtime)
- **Docker & Docker Compose** (Optional, to run containerized with MongoDB)

## 🚀 How to Run

### Option 1: Via Docker (Recommended)

The simplest way to run the full application (App + Database) is using Docker Compose.

```bash
docker compose up --build
```
The application will be available at `http://localhost:3000`.

### Option 2: Run Locally (Development)

Ensure you have a MongoDB instance running locally or adjust the connection string in the code/environment variables.

1.  **Install dependencies and compile:**
    ```bash
    ./gradlew build
    ```

2.  **Run the application in development mode:**
    ```bash
    ./gradlew jsNodeDevelopmentRun --continuous
    ```
    This will start the Node.js server compiled from Kotlin.

## 🏗️ Architecture and Structure

The project follows a classic layered architecture (Controller-Service-Repository), adapted for the Kotlin JS ecosystem.

*   `src/jsMain/kotlin/`: Main source code.
    *   `config/`: Application configuration.
    *   `di/`: Dependency Injection setup (Koin).
    *   `domain/`: Core business logic and interfaces.
        *   `application/service/`: Application service implementations.
        *   `model/`: Domain models.
        *   `repository/`: Repository interfaces.
        *   `service/`: Service interfaces.
    *   `infrastructure/`: Technical implementation and external adapters.
        *   `database/`: Database connection logic.
        *   `externals/`: **The core of interoperability.** Wrappers for JS libraries (Express, Mongoose, Node).
        *   `logging/`: Logging utilities.
        *   `persistence/`: Mongoose models and repository implementations.
        *   `web/`: Web layer components.
            *   `controller/`: HTTP Request Handlers (Controllers).
            *   `routes/`: Route definitions.
            *   `Middleware.kt`: Express middlewares.

## 🔍 How Interoperability Works (Kotlin ↔ JS)

The main barrier and also the greatest feature of Kotlin JS is the use of `external` modifiers.

To use a JS library like `express`, importing it is not enough; it is necessary to declare its interfaces in Kotlin so the compiler understands the types.

Example (`Express.kt`):
```kotlin
@JsModule("express")
@JsNonModule
external fun expressApplication(): ExpressApplication

external interface ExpressApplication {
    fun use(middleware: dynamic)
    fun get(path: String, handler: (Request, Response, NextFunction) -> Unit)
    // ...
}
```
This allows us to use native Node libraries with Kotlin's type safety.

## 📊 Pros and Cons (vs TypeScript)

Based on the exploration of this project:

### ✅ Pros
*   **Coroutines:** Concurrency model is much more robust and readable than pure Promises/async-await.
*   **Type System:** Kotlin's type system is stricter and safer at compile time (real null-safety) than TypeScript.
*   **Unified Ecosystem:** Possibility to share business logic (Data Classes, Validations, Rules) with Android/JVM/iOS applications via Kotlin Multiplatform.
*   **Gradle:** Powerful and reproducible dependency and build management.

### ❌ Cons / Challenges
*   **Interoperability Overhead:** It is necessary to write or generate wrappers (`externals`) for every JS library you want to use. There is no repository as vast as `@types/` for Kotlin JS on Node.
*   **Learning Curve:** Node.js developers need to learn Gradle and Kotlin.
*   **Smaller Ecosystem:** Fewer examples and tutorials focused on *Kotlin on Node.js Backend*.
*   **Developer Experience (DX):** The feedback loop (compile Gradle -> run) can be slower than native JS tools (like `tsx` or `nodemon`) in small projects.

## 🛠️ Technologies Used

*   **Language:** Kotlin 2.x (JS Target)
*   **Runtime:** Node.js
*   **Web Framework:** Express.js (via wrapper)
*   **Database:** MongoDB with Mongoose (via wrapper)
*   **Dependency Injection:** Koin
*   **Build:** Gradle (Kotlin DSL)
*   **Tests:** Kotest

## 📡 API Usage (Curl Examples)

Here are some examples of how to interact with the API using `curl`.

### 1. Health Check
```bash
curl -X GET http://localhost:3000/
```

### 2. Create a Contact
```bash
curl -X POST http://localhost:3000/api/contacts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "nickname": "Johnny",
    "email": "john.doe@example.com"
  }'
```

### 3. List All Contacts
```bash
curl -X GET http://localhost:3000/api/contacts
```

### 4. Get a Contact by ID
Replace `:id` with the actual ID returned from the creation step.
```bash
curl -X GET http://localhost:3000/api/contacts/:id
```

### 5. Update a Contact
Replace `:id` with the actual ID.
```bash
curl -X PUT http://localhost:3000/api/contacts/:id \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe Updated",
    "nickname": "Johnny",
    "email": "john.updated@example.com"
  }'
```

### 6. Delete a Contact
Replace `:id` with the actual ID.
```bash
curl -X DELETE http://localhost:3000/api/contacts/:id
```

## 🔮 Possible Improvements (Next Steps)

This PoC identifies several areas where the development experience and code quality could be enhanced to truly validate KotlinJS as a viable alternative. The next steps are categorized by their impact:

### 🛠️ Developer Experience (DX) & Tooling
- [ ] **Hot Reloading (Fast Feedback Loop):** Configure a workflow (e.g., combining Gradle continuous build `./gradlew -t build` with `nodemon`) to match the quick restart experience of TypeScript (`tsx watch`).
- [ ] **Debugging & Source Maps:** Ensure source maps are correctly configured and test debugging the Node.js process directly from IntelliJ IDEA or VSCode, with breakpoints hitting the `.kt` files.
- [ ] **CI/CD Pipeline:** Set up GitHub Actions to build, lint, and test the application automatically on every push, ensuring the Kotlin-to-JS compilation chain remains stable.
- [ ] **Documentation:** Add a way to document the project using KDoc comments and generate documentation with Dokka.

### 🌉 Interoperability & Type Safety
- [ ] **Automated Wrapper Generation:** Investigate tools to generate Kotlin external declarations from TypeScript Definition files (`.d.ts`) to significantly reduce manual effort in maintaining `infrastructure/externals` (e.g., Karakum).
- [ ] **Enhanced Type Safety:** Reduce the usage of `dynamic` types in the current Express/Mongoose wrappers by defining more strict external interfaces, potentially leveraging Kotlin's sealed classes.
- [ ] **Standardized Serialization:** Create a unified abstraction for `JSON <-> Object` conversion to verify `encodeToDynamic`/`json()` calls are consistent and to handle edge cases (like `_id` generation) in a single place.

### 🏗️ Architecture & Testing
- [ ] **Integration Testing (Testcontainers):** Expand the test suite to include integration tests that spin up a real MongoDB instance (e.g., via Testcontainers for Node) to verify the data access layer thoroughly.
- [ ] **Native MongoDB Driver:** Evaluate replacing Mongoose with wrappers for the official `mongodb` Node.js driver. This could reduce the overhead of Mongoose's object mapping and provide a more direct, potentially more type-safe data access layer.
- [ ] **Lightweight JS Frameworks (Alternative to Express):** Explore wrapping faster, modern Node.js frameworks like **Fastify** or **Hono** to compare performance and interoperability ease against Express.

### 🚀 Performance & Future-proofing
- [ ] **Benchmarking vs TypeScript:** Create a parallel, simplified TypeScript version of the same API to compare build times, memory footprint, cold start, and throughput (using tools like `autocannon` or `k6`).
- [ ] **Serverless & Edge Environments (Bun/Deno/Cloudflare):** Evaluate compiling to Wasm or strict ESM to run the backend in alternative JS runtimes like Bun, Deno, or Edge computing (Cloudflare Workers).
- [ ] **Kotlin Wasm / WASI Exploration:** With Node.js improving Wasm support, explore compiling Kotlin directly to WebAssembly to potentially bypass JavaScript overhead altogether.
- [ ] **Observability:** Implement logging and monitoring using a Node.js compatible library like OpenTelemetry (via JS wrappers) to track application health and performance metrics.

---
*This project serves as a knowledge base for migrations or new projects wishing to leverage Kotlin in the Server-side JavaScript environment.*
