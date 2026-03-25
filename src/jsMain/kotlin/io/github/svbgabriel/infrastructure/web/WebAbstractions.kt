package io.github.svbgabriel.infrastructure.web

import io.github.svbgabriel.infrastructure.web.openapi.OpenApiInfo
import io.github.svbgabriel.infrastructure.web.openapi.OpenApiRegistry
import io.github.svbgabriel.infrastructure.web.openapi.Operation
import io.konform.validation.Validation
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Abstraction for the Web Request and Response context.
 * This interface is used to interact with the request and send responses in a type-safe way.
 */
interface WebContext {
    /**
     * Map of path parameters from the current route.
     */
    val params: Map<String, String>

    /**
     * Map of query parameters from the request URL.
     */
    val query: Map<String, String>

    /**
     * Map of request headers.
     */
    val headers: Map<String, List<String>>

    /**
     * Receives and deserializes the request body.
     * @param serializer The serializer to use for deserialization.
     * @return The deserialized body of type [T].
     * @throws Exception if deserialization fails or body is missing.
     */
    suspend fun <T : Any> receive(serializer: KSerializer<T>): T

    /**
     * Sends a response with the given status and body.
     * @param status The HTTP status to send.
     * @param body The body to send.
     * @param serializer The serializer to use for serialization.
     */
    suspend fun <T : Any> respond(status: HttpStatus, body: T, serializer: KSerializer<T>)

    /**
     * Sends an empty response with the given status.
     * @param status The HTTP status to send.
     */
    suspend fun respondStatus(status: HttpStatus)

    /**
     * Sends an error response with the given status and message.
     * @param status The HTTP status to send.
     * @param message The error message to include in the response.
     */
    suspend fun respondError(status: HttpStatus, message: String)
}

/**
 * Extension for reified type safe receive.
 * Automatically resolves the serializer for type [T].
 * @return The deserialized body of type [T].
 */
suspend inline fun <reified T : Any> WebContext.receive(): T = receive(serializer<T>())

/**
 * Extension for reified type safe respond.
 * Automatically resolves the serializer for type [T].
 * @param status The HTTP status to send.
 * @param body The body to send.
 */
suspend inline fun <reified T : Any> WebContext.respond(status: HttpStatus, body: T) = respond(status, body, serializer<T>())

/**
 * Extension for body validation using Konform.
 * Receives the body and validates it using the provided [validation] rules.
 * @param validation The validation rules to apply.
 * @return The validated body of type [T].
 * @throws BadRequestException if validation fails.
 */
suspend inline fun <reified T : Any> WebContext.body(validation: Validation<T>): T {
    val body = receive<T>()
    val result = validation(body)
    if (!result.isValid) {
        val messages = result.errors.joinToString(", ") { it.message }
        throw BadRequestException(messages)
    }
    return body
}

/**
 * Abstraction for the Web Application.
 */
interface WebApplication {
    /**
     * Whether to use default middlewares (like JSON parsing) automatically.
     */
    var useDefaultMiddlewares: Boolean

    /**
     * Registers a global middleware handler.
     * @param handler The suspend function to handle the request.
     */
    fun use(handler: suspend (WebContext) -> Unit)

    /**
     * Registers a custom exception handler for a specific exception class.
     * @param exceptionClass The KClass of the exception to handle.
     * @param handler The suspend function to handle the exception.
     */
    fun <T : Throwable> exception(exceptionClass: kotlin.reflect.KClass<T>, handler: suspend WebContext.(T) -> Unit)

    /**
     * Defines a routing block for the given path.
     * @param path The base path for the routes.
     * @param builder The routing builder DSL.
     */
    fun route(path: String, builder: RoutingBuilder.() -> Unit)

    /**
     * Registers a callback that will be called whenever a route is registered.
     * @param callback The function to be called with the method, path, and OpenAPI operation.
     */
    fun onRouteRegistered(callback: (method: String, path: String, openApi: Operation?) -> Unit)

    /**
     * Serves the Swagger UI at the specified path.
     * @param path The path to serve the Swagger UI at.
     * @param info The OpenAPI info.
     * @param registry The OpenAPI registry containing all routes.
     */
    fun serveSwagger(path: String, info: OpenApiInfo, registry: OpenApiRegistry)

    /**
     * Registers a route in the underlying engine.
     * @param method The HTTP method (GET, POST, etc.).
     * @param path The full path for the route.
     * @param openApi Optional OpenAPI configuration for the route.
     * @param handler The suspend function to handle the request.
     */
    fun registerRoute(method: String, path: String, openApi: Operation?, handler: suspend WebContext.() -> Unit)

    /**
     * Installs a plugin or a set of configurations to the application.
     * @param plugin The configuration function.
     */
    fun install(plugin: WebApplication.() -> Unit) {
        plugin()
    }

    /**
     * Starts the web server on the specified port.
     * @param port The port to listen on.
     * @param callback A function to be called when the server starts.
     * @return A [WebServer] instance to control the server.
     */
    fun listen(port: Int, callback: () -> Unit): WebServer
}

/**
 * Abstraction for the underlying Web Server.
 */
interface WebServer {
    /**
     * Closes the server.
     * @param callback A function to be called when the server is closed.
     */
    fun close(callback: () -> Unit = {})
}

/**
 * Extension for reified exception handling.
 * Automatically resolves the KClass for type [T].
 * @param handler The suspend function to handle the exception.
 */
inline fun <reified T : Throwable> WebApplication.exception(noinline handler: suspend WebContext.(T) -> Unit) {
    exception(T::class, handler)
}

/**
 * DSL Builder for Routing.
 * @property webApp The web application instance.
 * @property basePath The base path for all routes in this builder.
 */
class RoutingBuilder(private val webApp: WebApplication, private val basePath: String = "") {
    /**
     * Creates a nested routing block.
     * @param path The sub-path.
     * @param builder The routing builder DSL for the nested path.
     */
    fun route(path: String, builder: RoutingBuilder.() -> Unit) {
        val nextPath = if (basePath.endsWith("/") && path.startsWith("/")) {
            basePath + path.substring(1)
        } else if (!basePath.endsWith("/") && !path.startsWith("/") && basePath.isNotEmpty() && path.isNotEmpty()) {
            "$basePath/$path"
        } else {
            basePath + path
        }
        RoutingBuilder(webApp, nextPath).apply(builder)
    }

    /**
     * Registers a GET route.
     * @param path The path for the route.
     * @param openApi Optional OpenAPI configuration for the route.
     * @param handler The suspend function to handle the request.
     */
    fun get(path: String = "", openApi: Operation? = null, handler: suspend WebContext.() -> Unit) = addRoute("GET", path, openApi, handler)

    /**
     * Registers a POST route.
     * @param path The path for the route.
     * @param openApi Optional OpenAPI configuration for the route.
     * @param handler The suspend function to handle the request.
     */
    fun post(path: String = "", openApi: Operation? = null, handler: suspend WebContext.() -> Unit) = addRoute("POST", path, openApi, handler)

    /**
     * Registers a PUT route.
     * @param path The path for the route.
     * @param openApi Optional OpenAPI configuration for the route.
     * @param handler The suspend function to handle the request.
     */
    fun put(path: String = "", openApi: Operation? = null, handler: suspend WebContext.() -> Unit) = addRoute("PUT", path, openApi, handler)

    /**
     * Registers a DELETE route.
     * @param path The path for the route.
     * @param openApi Optional OpenAPI configuration for the route.
     * @param handler The suspend function to handle the request.
     */
    fun delete(path: String = "", openApi: Operation? = null, handler: suspend WebContext.() -> Unit) = addRoute("DELETE", path, openApi, handler)

    /**
     * Registers a PATCH route.
     * @param path The path for the route.
     * @param openApi Optional OpenAPI configuration for the route.
     * @param handler The suspend function to handle the request.
     */
    fun patch(path: String = "", openApi: Operation? = null, handler: suspend WebContext.() -> Unit) = addRoute("PATCH", path, openApi, handler)

    /**
     * Internal helper to register a route with a specific HTTP method.
     */
    private fun addRoute(method: String, path: String, openApi: Operation?, handler: suspend WebContext.() -> Unit) {
        val fullPath = if (basePath.endsWith("/") && path.startsWith("/")) {
            basePath + path.substring(1)
        } else if (!basePath.endsWith("/") && !path.startsWith("/") && basePath.isNotEmpty() && path.isNotEmpty()) {
            "$basePath/$path"
        } else {
            basePath + path
        }
        // This will be implemented in the concrete WebApplication
        webApp.registerRoute(method, fullPath, openApi, handler)
    }
}

/**
 * Base class for WebApplication implementations to handle route registration.
 */
abstract class AbstractWebApplication : WebApplication {
    private val exceptionHandlers = mutableMapOf<kotlin.reflect.KClass<out Throwable>, suspend WebContext.(Throwable) -> Unit>()

    override fun registerRoute(method: String, path: String, openApi: Operation?, handler: suspend WebContext.() -> Unit) {
        // Concrete implementations will override this to perform actual registration
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Throwable> exception(exceptionClass: kotlin.reflect.KClass<T>, handler: suspend WebContext.(T) -> Unit) {
        exceptionHandlers[exceptionClass] = handler as suspend WebContext.(Throwable) -> Unit
    }

    /**
     * Handles an exception by finding the appropriate handler or using a fallback.
     * @param context The current web context.
     * @param error The exception that occurred.
     */
    protected suspend fun handleException(context: WebContext, error: Throwable) {
        val handler = findHandler(error::class)
        if (handler != null) {
            handler(context, error)
        } else {
            // Fallback for unhandled exceptions
            context.respondError(HttpStatus.INTERNAL_SERVER_ERROR, error.message ?: "Internal Server Error")
        }
    }

    /**
     * Finds the best matching exception handler for the given [exceptionClass].
     * Supports exact matches and superclass matches.
     * @param exceptionClass The exception class to search for.
     * @return The handler function if found, null otherwise.
     */
    private fun findHandler(exceptionClass: kotlin.reflect.KClass<out Throwable>): (suspend WebContext.(Throwable) -> Unit)? {
        // Try an exact match
        exceptionHandlers[exceptionClass]?.let { return it }

        // Try superclasses match
        return exceptionHandlers.entries
            .filter { it.key.isInstance(exceptionClass) || it.key.js.asDynamic().isAssignableFrom(exceptionClass.js) == true }
            .map { it.value }
            .firstOrNull()
    }
}
