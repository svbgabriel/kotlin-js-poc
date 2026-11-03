package io.github.svbgabriel.swagger

import io.github.svbgabriel.express.RequestHandler

/**
 * External module for swagger-ui-express.
 */
@JsModule("swagger-ui-express")
@JsNonModule
external val swaggerUi: SwaggerUi

/**
 * Configuration options for Swagger UI setup.
 */
external interface SwaggerUiOptions {
    var customCss: String?
    var customCssUrl: String?
    var customSiteTitle: String?
    var customfavIcon: String?
    var displayRequestDuration: Boolean?
    var validatorUrl: String?
}

/**
 * Represents the Swagger UI express module.
 */
external interface SwaggerUi {
    /**
     * Middleware to serve Swagger UI.
     */
    val serve: RequestHandler

    /**
     * Returns middleware to setup Swagger UI with the given document.
     * @param swaggerDoc The swagger document.
     * @param options The options for setting up Swagger UI.
     * @return A [RequestHandler].
     */
    fun setup(swaggerDoc: OpenApiSpec, options: SwaggerUiOptions = definedExternally): RequestHandler
}
