package io.github.svbgabriel.infrastructure.web.openapi

import io.github.svbgabriel.swagger.OpenApiInfo
import io.github.svbgabriel.swagger.OpenApiSpec
import io.github.svbgabriel.swagger.Operation
import kotlin.js.json

class OpenApiRegistry {
    private val paths = json()

    fun registerRoute(
        method: String,
        path: String,
        config: Operation?
    ) {
        if (config == null) return

        val normalizedPath = path.replace(Regex("/{2,}"), "/")
            .let { if (it.length > 1) it.removeSuffix("/") else it }
        val expressPathToOpenApi = normalizedPath.replace(Regex(":(\\w+)"), "{$1}")

        val pathItem = paths[expressPathToOpenApi].asDynamic() ?: json()
        pathItem[method.lowercase()] = config
        paths[expressPathToOpenApi] = pathItem
    }

    fun generateSpec(info: OpenApiInfo): OpenApiSpec {
        return OpenApiSpec(
            openapi = "3.0.0",
            info = info,
            paths = paths,
            components = undefined
        )
    }
}
