package io.github.svbgabriel.infrastructure.externals.swagger

import io.github.svbgabriel.infrastructure.externals.express.RequestHandler

@JsModule("swagger-ui-express")
@JsNonModule
external val swaggerUi: SwaggerUi

external interface SwaggerUi {
    val serve: RequestHandler
    fun setup(swaggerDoc: dynamic, options: dynamic = definedExternally): RequestHandler
}
