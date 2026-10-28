# AGENTS.md — Guidelines for the Contacts KotlinJS Project

This document defines the coding standards, architecture rules, and best practices for the **Contacts** project — a Proof of Concept that validates using **Kotlin Multiplatform (JS target)** as a backend alternative to TypeScript in the Node.js ecosystem.

---

## Project Overview

- **Language:** Kotlin 2.x (JS Target)
- **Runtime:** Node.js (v22+)
- **Build:** Gradle with Kotlin DSL
- **Web Framework:** Express.js (via Kotlin wrappers)
- **Database:** MongoDB with Mongoose (via Kotlin wrappers)
- **DI:** Koin
- **Testing:** Kotest + Mokkery
- **Validation:** Konform
- **Serialization:** kotlinx.serialization
- **Architecture:** Hexagonal (Ports & Adapters)

### Key Commands

```bash
# Build the project
./gradlew build

# Run the application in development mode (with hot reload)
./gradlew jsNodeDevelopmentRun --continuous

# Run all tests (Unit, Integration, E2E)
./gradlew jsNodeTest

# Run a specific test
./gradlew jsNodeTest --tests "ContactControllerTest"

# Run via Docker Compose (app + MongoDB)
docker compose up --build
```

---

## Architecture

The project follows **Hexagonal Architecture (Ports & Adapters)** with a layered structure:

```
src/jsMain/kotlin/io/github/svbgabriel/
├── Application.kt              # Entry point — composes plugins
├── config/                     # Application configuration
├── di/                         # Koin dependency injection modules
├── domain/                     # Core business logic (inner circle)
│   ├── model/                  # Domain models (data classes)
│   ├── ports/                  # Interfaces (Repository & Service ports)
│   │   ├── repository/         # Repository interfaces
│   │   └── service/            # Service interfaces
│   └── service/                # Service implementations
└── infrastructure/             # External adapters (outer circle)
    ├── config/                 # Environment variable mapping
    ├── database/               # MongoDB/Mongoose connection & plugins
    ├── health/                 # Health check implementations
    ├── logging/                # Logging abstractions
    ├── persistence/            # Repository implementations & Mongoose schemas
    └── web/                    # Web layer
        ├── controller/         # HTTP request handlers
        │   └── dto/            # Request/Response DTOs
        ├── openapi/            # OpenAPI/Swagger integration
        ├── plugin/             # Infrastructure plugins (dotenv, defaults)
        ├── routes/             # Route definitions (DSL-based)
        ├── WebAbstractions.kt  # Agnostic web interfaces
        ├── ExpressImplementation.kt  # Express-specific implementation
        └── WebFactory.kt       # Web server factory
```

### Architecture Rules

1. **Domain is independent.** The `domain/` package must never import from `infrastructure/`.
2. **Ports define contracts.** Interfaces live in `domain/ports/`. Implementations live in `infrastructure/`.
3. **Dependency Injection via Koin.** All wiring happens in `di/Modules.kt`. Use `single {}` for singletons.
4. **Plugin-based setup.** The `Application.kt` entry point installs features via `install*()` extension functions on `WebApplication`.
5. **Web abstraction layer.** All HTTP interaction goes through `WebContext` and `WebApplication` interfaces, not directly through Express types.

---

## Kotlin Coding Standards

### Naming Conventions

- **Classes & Interfaces:** `PascalCase` (e.g., `ContactRepository`, `ContactServiceImpl`)
- **Functions & Variables:** `camelCase` (e.g., `findAll`, `contactId`)
- **Constants:** `SCREAMING_SNAKE_CASE` in `companion object` (e.g., `CONTACT_NOT_FOUND`)
- **Files:** One top-level class per file, named after the class. For top-level functions, name the file after the primary function (e.g., `MongooseUtils.kt`).
- **Packages:** Follow `io.github.svbgabriel.<layer>.<sublayer>` structure.

### Code Style

- **Indentation:** 4 spaces (no tabs).
- **Line length:** Prefer lines under 120 characters.
- **Imports:** Grouped and sorted — standard library, Kotlinx, third-party, project imports. Use IDE auto-import.
- **Trailing commas:** Enabled for multi-line lists and parameter lists.

### Null Safety

- **Prefer non-nullable types** whenever possible.
- Use `String?` only when `null` is a meaningful value (e.g., optional fields, database IDs before creation).
- Use `takeIf {}`, `let {}`, `run {}`, `also {}`, and `apply {}` scope functions appropriately instead of explicit null checks.
- Avoid `!!` — use safe calls (`?.`) and the Elvis operator (`?:`) instead.

### Data Classes

- Use `data class` for immutable domain models and DTOs.
- Place stable, frequently-used properties first in the primary constructor.
- Use factory methods (`fromDomain()`, `toDomain()`) on DTOs for mapping between layers.

### Coroutines

- **All I/O functions must be `suspend`.** Repository methods, service methods, and controller handlers are all suspend functions.
- Use `suspend` at the boundary, not just internally with `runBlocking` or `GlobalScope`.
- Avoid `GlobalScope` — rely on the coroutine context propagated by the web framework.
- For concurrent operations, prefer `coroutineScope {}` or `supervisorScope {}` over ad-hoc launching.

### Extension Functions

- Use extension functions to add behavior to interfaces without modifying them (e.g., `WebContext.body()`, `WebContext.respond()`).
- Place extensions in the same package as the receiver type when possible.
- Use `inline` with `reified` type parameters when serializer resolution is needed.

### Interfaces & Abstractions

- Define interfaces for replaceable components (repositories, services, web context).
- Provide KDoc documentation on all public interfaces and their members.
- Use default implementations in interfaces sparingly; prefer explicit implementations.

### Exception Handling

- Define custom exceptions for domain-specific errors (e.g., `BadRequestException`).
- Let exceptions propagate through suspend functions; handle them at the web layer with middleware.
- Use `companion object` constants for error messages in controllers.

---

## KotlinJS-Specific Guidelines

### External Declarations (`external`)

The project wraps Node.js libraries using Kotlin `external` declarations. These live in separate modules under `ktypes-*`:

```
ktypes-express/     # Express.js wrappers
ktypes-mongoose/    # Mongoose wrappers
ktypes-node/        # Node.js process & utilities
ktypes-dotenv/      # dotenv wrapper
ktypes-swagger-ui/  # Swagger UI wrapper
ktypes-javascript/  # General JS utilities
ktypes-testcontainers/  # Testcontainers for testing
```

**Rules for external declarations:**

1. **Use `@JsModule` and `@JsNonModule`** annotations to import ES modules:
   ```kotlin
   @JsModule("express")
   @JsNonModule
   external fun expressApplication(): ExpressApplication
   ```
2. **Prefer typed `external interface`** over `dynamic` whenever possible. Minimize `dynamic` usage to reduce runtime errors.
3. **Document every external declaration** with KDoc explaining the underlying JS library behavior.
4. **Use `typealias`** for function signatures and common patterns (e.g., `typealias RequestHandler = (Request, Response, NextFunction) -> Unit`).
5. **Keep external wrappers thin.** They should map 1:1 with the JS library API. Add Kotlin-side convenience in separate files, not in the external declarations.

### JS Interop Types

- Use `kotlin.js.Json` for JSON literals.
- Use `kotlinx.js.JsPlainObject` for plain JS objects.
- Use `kotlin.js.arrayOf()` and `kotlin.js.json()` for creating JS-compatible structures.
- Use `dynamic` only as a last resort when no typed interface exists.

### ES Modules

- The project uses `useEsModules()` in `build.gradle.kts`. All compiled output is `.mjs`.
- Ensure external declarations are compatible with ES module semantics.

### Serialization

- Use `kotlinx.serialization` with `@Serializable` annotations on DTOs and domain models that need JSON conversion.
- Use reified extension functions (`WebContext.respond<T>()`) for automatic serializer resolution.
- Handle `_id` field mapping between MongoDB and Kotlin explicitly in entity-to-domain mapping.

---

## Testing Guidelines

### Framework

- **Kotest** with `FunSpec` style for test structure.
- **Mokkery** for mocking dependencies.
- Use `shouldBe` matchers for assertions.

### Test Organization

- Tests live in `src/jsTest/kotlin/`, mirroring the `src/jsMain/kotlin/` package structure.
- Name test files with the `Test` suffix (e.g., `EnvironmentTest.kt`).

### Test Patterns

```kotlin
class EnvironmentTest : FunSpec({
    test("should get default value when variable is not present") {
        val result = Environment.get("NON_EXISTENT", "default")
        result shouldBe "default"
    }
})
```

- Use descriptive test names: `"should <expected behavior> when <condition>"`.
- Clean up test state (e.g., reset environment variables) after each test.
- For integration tests, use **Testcontainers** to spin up real MongoDB instances.
- For E2E tests, start the Express server and make real HTTP calls.

### Test Types

| Type | Location | Purpose |
|------|----------|---------|
| Unit | `src/jsTest/...` (mirrors main) | Test business logic in isolation |
| Integration | `src/jsTest/.../infrastructure/` | Verify persistence with MongoDB via Testcontainers |
| E2E | `src/jsTest/.../e2e/` | Full API flow with real HTTP calls |

---

## Dependency Injection (Koin)

- Define modules in `di/Modules.kt` using `module {}` DSL.
- Use `single {}` for singleton-scoped dependencies (repositories, services, controllers).
- Bind implementations to interfaces: `single<ContactRepository> { ContactRepositoryImpl() }`.
- Use `get()` to resolve dependencies within a module definition.
- Install the module in `Application.kt` via `installDependencyInjectionModule { modules(appModule) }`.

---

## Web Layer Conventions

### Route Definition

- Use the DSL-based `route()` extension on `WebApplication`.
- Define OpenAPI documentation inline with each route using `Operation`.
- Inject controllers via `KoinComponent` and `by inject()`.

### Controller Pattern

- Controllers are classes that receive the service via constructor injection.
- Each HTTP verb maps to a `suspend fun` method on the controller.
- Use `WebContext` extension functions for body parsing, parameter extraction, and response sending.
- Validate request bodies using Konform validators passed to `context.body(validation)`.

### DTOs

- Request DTOs live in `controller/dto/request/`.
- Response DTOs live in `controller/dto/response/`.
- Provide `toDomain()` and `fromDomain()` mapping methods.

### HTTP Status Codes

- Use the type-safe `HttpStatus` sealed class instead of raw integers.
- Respond with `context.respond(status, body)` or `context.respondStatus(status)`.

---

## Plugin Pattern

The application uses an `install*()` plugin pattern for modular setup:

```kotlin
fun WebApplication.installContactRoutes() { ... }
fun WebApplication.installOpenApi(info: OpenApiInfo) { ... }
fun WebApplication.installDatabase() { ... }
```

**Rules:**
- Each plugin is an extension function on `WebApplication`.
- Plugins are installed in `Application.kt` in dependency order.
- Keep plugins focused — one concern per plugin.

---

## Environment Configuration

- Use `Environment.get()`, `Environment.getInt()`, `Environment.getBoolean()` for type-safe access.
- Always provide default values for environment variables.
- Load `.env` files via the `installDotenv` plugin.
- Never hardcode secrets — use environment variables.

---

## Gradle & Build

- Use **Kotlin DSL** (`build.gradle.kts`) for all build scripts.
- Manage versions via **Gradle Version Catalog** (`libs.plugins`, `libs.*`).
- The project compiles to ES2015 target with source maps enabled.
- Yarn script execution is disabled for security (`ignoreScriptsProperty = true`).

### Multi-Module Structure

The project uses submodules for external wrappers:
- `ktypes-express`, `ktypes-mongoose`, `ktypes-node`, `ktypes-dotenv`, `ktypes-swagger-ui`, `ktypes-javascript`, `ktypes-testcontainers`

Each submodule is a Kotlin JS module that provides typed wrappers for a specific JS library. Reference them via `implementation(project(":ktypes-<name>"))`.

---

## Code Quality & Tooling (Planned)

The following tools are identified for future adoption:

- **ktlint / detekt:** Enforce consistent code style and detect code smells.
- **Kover:** Code coverage reporting for KotlinJS.
- **Dokka:** API documentation generation from KDoc.
- **Dukat / Karakum:** Automated generation of Kotlin external declarations from `.d.ts` files.

---

## Git & Commit Conventions

The project follows the **[Conventional Commits](https://www.conventionalcommits.org/)** standard for commit messages, ensuring a readable and automatable history.

### Format

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code refactoring without behavior change |
| `test` | Adding or fixing tests |
| `build` | Changes to the build system or dependencies |
| `ci` | Changes to CI/CD configurations |
| `docs` | Documentation only |
| `chore` | Maintenance tasks that do not fit other types |
| `perf` | Performance improvements |
| `style` | Formatting, punctuation, etc. (no code change) |

### Scopes

Use the scope to indicate the affected layer or module:

| Scope | Layer |
|-------|-------|
| `domain` | Business logic, models, ports, services |
| `infrastructure` | Adapters, persistence, database, logging |
| `web` | Controllers, DTOs, routes, web plugins, OpenAPI |
| `di` | Dependency injection configuration (Koin) |
| `config` | Environment and application configuration |
| `ktypes` | External wrappers (`ktypes-express`, `ktypes-mongoose`, etc.) |
| `build` | Gradle, Gradle DSL, version catalog |
| `docker` | Dockerfile, docker-compose |

### Examples

```
feat(web): add contact creation endpoint with validation
fix(domain): handle null phone number in contact model
refactor(infrastructure): extract mongoose schema to dedicated file
test(web): add integration tests for contact repository
docs: update AGENTS.md with conventional commits guide
build(ktypes): add ktypes-testcontainers module
```

### Rules

- **Messages in English.** Use English to maintain consistency with the Kotlin/JS ecosystem.
- **Present tense.** Use `"add contact endpoint"` instead of `"added contact endpoint"`.
- **No period** at the end of the title.
- **One commit, one change.** Keep commits focused on a single logical change.
- **Optional body.** Use the body when the description needs additional context or justification.

---

## Docker & Deployment

- The `Dockerfile` and `docker-compose.yml` provide containerized deployment.
- Docker Compose runs both the application and MongoDB.
- For production bundling, consider configuring Webpack, Rollup, or ESBuild via Gradle.

---

## Implementation Verification

All implementations must be verified with tests and updated documentation:

### Testing Requirements

- **Every new feature or bug fix** must include tests that verify the expected behavior.
- **Unit tests** are required for business logic in the `domain/` layer.
- **Integration tests** are required when the implementation interacts with external systems (database, file system, external APIs).
- **E2E tests** are required for new or modified API endpoints to verify the full request-response flow.
- Tests must be added before or alongside the implementation — not after.
- If existing tests are insufficient to cover the change, extend them before proceeding.

### Documentation Requirements

- **Update `README.md`** when a new feature, endpoint, or configuration option is added.
- **Update `AGENTS.md`** when a new coding standard, architecture decision, or convention is introduced.
- **Update KDoc** for all new public APIs, interfaces, and external declarations.
- Documentation changes should be included in the same commit as the implementation, not in a separate commit.

---

## Key Principles Summary

1. **Domain-first:** Business logic lives in `domain/` and knows nothing about infrastructure.
2. **Abstraction over implementation:** Use interfaces (`WebContext`, `ContactRepository`) — never depend on Express or Mongoose types directly in domain or controller code.
3. **Type safety:** Leverage Kotlin's type system. Minimize `dynamic`. Use `external interface` for JS interop.
4. **Coroutines everywhere:** All I/O is `suspend`. No callbacks, no raw Promises.
5. **Plugin-based composition:** Features are installed via `install*()` extensions.
6. **Test at every layer:** Unit tests for logic, integration tests for persistence, E2E tests for the full API.
7. **KDoc on public APIs:** Document interfaces, external declarations, and extension functions.
8. **Verify with tests:** Every implementation must be validated with tests; add tests when needed.
9. **Update documentation:** Changes to the codebase must be reflected in `README.md`, `AGENTS.md`, and KDoc as applicable.
