package io.github.svbgabriel.infrastructure.web.openapi

import io.github.svbgabriel.infrastructure.web.WebApplication
import io.github.svbgabriel.swagger.OpenApiInfo

fun WebApplication.installOpenApi(
    info: OpenApiInfo,
    path: String = "/api-docs"
) {
    val registry = OpenApiRegistry()

    this.onRouteRegistered { method, routePath, openApi ->
        registry.registerRoute(method, routePath, openApi)
    }

    this.serveSwagger(path, info, registry)
}
