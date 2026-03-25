package io.github.svbgabriel.infrastructure.web.openapi

import io.github.svbgabriel.infrastructure.externals.swagger.swaggerUi
import io.github.svbgabriel.infrastructure.web.ExpressWebApplication
import io.github.svbgabriel.infrastructure.web.WebApplication

fun WebApplication.installOpenApi(
    info: OpenApiInfo,
    path: String = "/api-docs"
) {
    val registry = OpenApiRegistry()

    if (this is ExpressWebApplication) {
        this.onRouteRegistered { method, routePath, openApi ->
            registry.registerRoute(method, routePath, openApi)
        }

        // Serve Swagger UI
        this.expressApp.use(path, swaggerUi.serve)

        // Serve the raw spec as JSON for tests/debugging
        val jsonPath = if (path.endsWith("/")) "${path}json" else "$path/json"
        this.expressApp.get(jsonPath) { _, res, _ ->
            val spec = registry.generateSpec(info)
            res.json(spec)
        }

        this.expressApp.get(path) { req, res, next ->
            val spec = registry.generateSpec(info)
            try {
                swaggerUi.setup(spec)(req, res, next)
            } catch (e: Exception) {
                next(e)
            }
        }
    }
}
