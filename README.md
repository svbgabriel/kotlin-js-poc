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

The simplest way to run the full application (App and Database) is using Docker Compose.

```bash
docker compose up --build
```
The application will be available at `http://localhost:3000`.

### Option 2: Run Locally (Development)

Ensure you have a MongoDB instance running locally or adjust the connection string in the code/environment variables.

1.  **Configure environment variables:**
    Copy the example file and adjust the values:
    ```bash
    cp .env.example .env
    ```

2.  **Install dependencies and compile:**
    ```bash
    ./gradlew build
    ```

3.  **Run the application in development mode:**
    ```bash
    ./gradlew jsNodeDevelopmentRun --continuous
    ```
    This will start the Node.js server compiled from Kotlin.

## 🏗️ Architecture and Structure

The project follows a classic layered architecture (Controller-Service-Repository), adapted for the Kotlin JS ecosystem.

*   `src/jsMain/kotlin/io/github/svbgabriel/`: Main source code.
    *   `Application.kt`: Entry point of the application.
    *   `config/`: Application configuration and environment variables mapping.
    *   `di/`: Dependency Injection setup using Koin.
    *   `domain/`: Core business logic and interfaces (Hexagonal Architecture / Ports & Adapters).
        *   `model/`: Domain models (e.g., `Contact`).
        *   `ports/`: Interfaces for external communication (Repository and Service ports).
        *   `service/`: Implementation of domain services (Business logic).
    *   `infrastructure/`: Technical implementation and external adapters.
        *   `database/`: MongoDB connection logic and Mongoose setup.
        *   `externals/`: **The core of interoperability.** Kotlin wrappers for JS libraries (Express, Mongoose, Node).
        *   `health/`: Health check implementations.
        *   `logging/`: Logging abstractions and implementations.
        *   `persistence/`: Repository implementations and Mongoose schemas.
        *   `web/`: Web layer abstraction and implementations.
            *   `WebAbstractions.kt`: Agnostic web server interfaces (`WebApplication`, `RoutingBuilder`).
            *   `ExpressImplementation.kt`: Express-specific implementation of the web abstractions.
            *   `WebFactory.kt`: Factory to instantiate the web server.
            *   `controller/`: HTTP Request Handlers and DTOs.
            *   `routes/`: Route definitions using the agnostic DSL.
            *   `openapi/`: OpenAPI/Swagger integration (Plugin, Registry, and Models).
            *   `plugin/`: Infrastructure setup (DI, Database, App-wide middlewares).
            *   `HttpStatus.kt`: Type-safe HTTP status codes.
            *   `Middleware.kt`: Web server middlewares.
            *   `Exceptions.kt`: Custom web-related exceptions.

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

## 📊 Pros and Cons (vs. TypeScript)

Based on the exploration of this project:

### ✅ Pros
*   **Coroutines:** Concurrency model is much more robust and readable than pure Promises/async-await.
*   **Type System:** Kotlin's type system is stricter and safer at compile time (real null-safety) than TypeScript.
*   **Unified Ecosystem:** Possibility to share business logic (Data Classes, Validations, Rules) with Android/JVM/iOS applications via Kotlin Multiplatform.
*   **Gradle:** Powerful and reproducible dependency and build management.
*   **Modern JS Interop:** Generation of ES Modules (`.mjs`) and support for modern Node.js features.

### ❌ Cons / Challenges
*   **Interoperability Overhead:** It is necessary to write or generate wrappers (`externals`) for every JS library you want to use. There is no repository as vast as `@types/` for Kotlin JS on Node.
*   **Learning Curve:** Node.js developers need to learn Gradle and Kotlin.
*   **Smaller Ecosystem:** Fewer examples and tutorials focused on *Kotlin on Node.js Backend*.
*   **Developer Experience (DX):** The feedback loop (compile Gradle → run) can be slower than native JS tools (like `tsx` or `nodemon`) in small projects.

## 🛠️ Technologies Used

*   **Language:** Kotlin 2.x (JS Target)
*   **Runtime:** Node.js
*   **Web Framework:** Express.js (via wrapper)
*   **Database:** MongoDB with Mongoose (via wrapper)
*   **Dependency Injection:** Koin
*   **Build:** Gradle (Kotlin DSL)
*   **Tests:** Kotest and Mokkery

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

### 7. API Documentation (Swagger)
The interactive API documentation is available at:
`http://localhost:3000/api-docs/`

You can also access the raw OpenAPI specification in JSON format:
`http://localhost:3000/api-docs/json`

## 🔮 Possible Improvements (Next Steps)

This PoC identifies several areas where the development experience and code quality could be enhanced to truly validate KotlinJS as a viable alternative. The next steps are categorized by their impact:

### 🛠️ Developer Experience (DX) & Tooling
- [X] **Hot Reloading (Fast Feedback Loop):** Configure a workflow (e.g., combining Gradle continuous build `./gradlew -t build` with `nodemon`) to match the quick restart experience of TypeScript (`tsx watch`).
- [X] **Debugging & Source Maps:** Ensure source maps are correctly configured and test debugging the Node.js process directly from IntelliJ IDEA or VSCode, with breakpoints hitting the `.kt` files.
- [X] **CI/CD Pipeline & Reports:** Set up reports (XML/HTML) to ensure the Kotlin-to-JS compilation chain remains stable and observable.
- [X] **API Documentation (Swagger/OpenAPI):** Integrated `swagger-ui-express` via an agnostic web abstraction to provide interactive documentation and JSON specification directly from Kotlin routing DSL.
- [X] **Environment Variables:** Support for `.env` files via `dotenv` integration, making local development easier and more aligned with the Node.js ecosystem.
- [ ] **Test Coverage:** When available, implement code coverage (e.g., Kover) to monitor the testing effectiveness of the Kotlin code. At the time of writing, there are none available for KotlinJS.
- [ ] **Code Quality:** Evaluate tools like `ktlint` or `detekt` to enforce consistent code style and identify potential issues.
- [ ] **Documentation:** Add a way to document the project using KDoc comments and generate documentation with Dokka.

### 🌉 Interoperability & Type Safety
- [ ] **Automated Wrapper Generation:** Investigate tools to generate Kotlin external declarations from TypeScript Definition files (`.d.ts`) to significantly reduce manual effort in maintaining `infrastructure/externals` (e.g., Dukat or Karakum). This is the biggest barrier to entry today.
- [ ] **Enhanced Type Safety:** Reduce the usage of `dynamic` types in the current Express/Mongoose wrappers by defining more strict external interfaces, potentially leveraging Kotlin's sealed classes.
- [ ] **Standardized Serialization:** Create a unified abstraction for `JSON <-> Object` conversion to verify `encodeToDynamic`/`json()` calls are consistent and to handle edge cases (like `_id` generation or Date mapping) in a single place.
- [ ] **Error Handling Interop:** Create standardized middleware to map Kotlin exceptions (like Konform validation errors or custom domain exceptions) automatically to structured Express JSON error responses.

### 🏗️ Architecture & Testing
- [X] **Integration Testing (Testcontainers):** Expand the test suite to include integration tests that spin up a real MongoDB instance (e.g., via Testcontainers for Node) to verify the data access layer thoroughly.
- [ ] **Production Bundling:** Configure a bundler (like Webpack, Rollup, or ESBuild) via Gradle to package the application and its dependencies into a single, optimized JS file for production deployment.
- [x] **Graceful Shutdown:** Handle SIGTERM/SIGINT signals to close database connections and stop the server cleanly.
- [ ] **Native MongoDB Driver:** Evaluate replacing Mongoose with wrappers for the official `mongodb` Node.js driver. This could reduce the overhead of Mongoose's object mapping and provide a more direct, potentially more type-safe data access layer.
- [ ] **Lightweight JS Frameworks (Alternative to Express):** Explore wrapping faster, modern Node.js frameworks like **Fastify** or **Hono** to compare performance and interoperability ease against Express.

### 🚀 Performance & Future-proofing
- [ ] **Benchmarking vs TypeScript:** Create a parallel, simplified TypeScript version of the same API to compare build times, memory footprint, cold start, and throughput (using tools like `autocannon` or `k6`).
- [ ] **Serverless & Edge Environments (Bun/Deno/Cloudflare):** Evaluate compiling to Wasm or strict ESM to run the backend in alternative JS runtimes like Bun, Deno, or Edge computing (Cloudflare Workers).
- [ ] **Kotlin Wasm / WASI Exploration:** With Node.js improving Wasm support, explore compiling Kotlin directly to WebAssembly to potentially bypass JavaScript overhead altogether.
- [ ] **Observability:** Implement logging and monitoring using a Node.js compatible library like OpenTelemetry (via JS wrappers) to track application health and performance metrics.

## 🧪 Testing

The project uses **Kotest** as the testing framework and **Mokkery** for mocking. The test suite includes unit, integration, and end-to-end (E2E) tests.

### Run all tests
To run all tests (Unit, Integration, and E2E) in the Node.js environment:
```bash
./gradlew jsNodeTest
```
E2E tests use **Testcontainers** to spin up a real MongoDB instance during execution.

### Test Reports
The project is configured to generate the following reports:
*   **JUnit XML:** Located at `build/test-results/jsNodeTest/` (ideal for CI/CD).
*   **HTML Report:** Located at `build/reports/tests/jsTest/`.

### Run specific tests
You can filter for specific tests using the `--tests` property:
```bash
./gradlew jsNodeTest --tests "ContactControllerTest"
```

### Test Types
*   **Unit Tests:** Located in `src/jsTest/kotlin/...`, these test business logic in isolation.
*   **Integration Tests:** Verify persistence with MongoDB (via Testcontainers).
*   **E2E (End-to-End):** Test the full API flow by starting the Express server and making real HTTP calls.

---
*This project serves as a knowledge base for migrations or new projects wishing to leverage Kotlin in the Server-side JavaScript environment.*
