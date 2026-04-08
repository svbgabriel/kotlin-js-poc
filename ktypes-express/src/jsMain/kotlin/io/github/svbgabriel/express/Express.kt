package io.github.svbgabriel.express

import kotlinx.js.JsPlainObject
import kotlin.js.Json

/**
 * External module for Express.js.
 * Provides access to the Express application and middleware.
 */
@JsModule("express")
@JsNonModule
external val expressMiddleware: ExpressMiddleware

/**
 * Creates an Express application.
 * @return An instance of [ExpressApplication].
 */
@JsModule("express")
@JsNonModule
external fun expressApplication(): ExpressApplication

/**
 * Type alias for the next function in Express middleware.
 */
typealias NextFunction = (Any?) -> Unit

/**
 * Type alias for an Express request handler.
 */
typealias RequestHandler = (Request, Response, NextFunction) -> Unit

/**
 * Type alias for an Express error request handler.
 */
typealias ErrorRequestHandler = (Any, Request, Response, NextFunction) -> Unit

/**
 * Represents an Express application instance.
 */
external interface ExpressApplication {
    /**
     * Mounts the specified middleware function.
     * @param middleware The middleware function.
     */
    fun use(middleware: RequestHandler)

    /**
     * Mounts the specified error handling middleware function.
     * @param errorHandler The error handling middleware function.
     */
    fun use(errorHandler: ErrorRequestHandler)

    /**
     * Mounts the specified middleware function at the specified path.
     * @param path The path at which the middleware should be mounted.
     * @param middleware The middleware function.
     */
    fun use(path: String, middleware: RequestHandler)

    /**
     * Starts a UNIX socket and listens for connections on the given path.
     * @param port The port to listen on.
     * @param callback The callback function to execute once the server starts.
     * @return A [Server] instance.
     */
    fun listen(port: Int, callback: () -> Unit): Server

    /**
     * Returns an instance of a single route, which you can then use to handle HTTP verbs with optional middleware.
     * @param path The path for the route.
     * @return A [Route] instance.
     */
    fun route(path: String): Route

    /**
     * Routes HTTP GET requests to the specified path with the specified callback functions.
     */
    fun get(path: String, handler: RequestHandler)

    /**
     * Routes HTTP POST requests to the specified path with the specified callback functions.
     */
    fun post(path: String, handler: RequestHandler)

    /**
     * Routes HTTP PUT requests to the specified path with the specified callback functions.
     */
    fun put(path: String, handler: RequestHandler)

    /**
     * Routes HTTP DELETE requests to the specified path with the specified callback functions.
     */
    fun delete(path: String, handler: RequestHandler)

    /**
     * Routes HTTP PATCH requests to the specified path with the specified callback functions.
     */
    fun patch(path: String, handler: RequestHandler)
}

/**
 * Represents Express middleware factory functions.
 */
external interface ExpressMiddleware {
    /**
     * Returns middleware that only parses JSON and only looks at requests where the Content-Type header matches the type option.
     */
    fun json(): RequestHandler

    /**
     * Returns middleware that only parses urlencoded bodies and only looks at requests where the Content-Type header matches the type option.
     */
    fun urlencoded(options: ExpressUrlEncodedOptions): RequestHandler
}

/**
 * Represents a route in Express.
 */
external interface Route {
    fun get(handler: RequestHandler): Route
    fun post(handler: RequestHandler): Route
    fun put(handler: RequestHandler): Route
    fun delete(handler: RequestHandler): Route
    fun patch(handler: RequestHandler): Route
}

/**
 * Represents an HTTP request in Express.
 */
external interface Request {
    /**
     * This property is an object containing properties mapped to the named route "parameters".
     */
    val params: Json

    /**
     * Contains key-value pairs of data submitted in the request body.
     */
    val body: Any?

    /**
     * This property is an object containing a property for each query string parameter in the route.
     */
    val query: Json

    /**
     * The HTTP request headers.
     */
    val headers: Json
}

/**
 * Represents an HTTP response in Express.
 */
external interface Response {
    /**
     * Sets the HTTP status for the response.
     */
    fun status(code: Int): Response

    /**
     * Sends a JSON response.
     */
    fun json(body: Any?): Response

    /**
     * Sends the HTTP response.
     */
    fun send(body: Any?): Response

    /**
     * Sets the response HTTP header field to value.
     */
    fun set(field: String, vararg value: String): Response
}

/**
 * Options for Express URL-encoded middleware.
 */
@JsPlainObject
external interface ExpressUrlEncodedOptions {
    /**
     * The "extended" syntax allows for rich objects and arrays to be encoded into the URL-encoded format.
     */
    val extended: Boolean?
}

/**
 * Represents the HTTP server returned by [ExpressApplication.listen].
 */
external interface Server {
    /**
     * Stops the server from accepting new connections.
     */
    fun close(callback: () -> Unit = definedExternally)

    /**
     * Returns the bound address, the address family name, and port of the server.
     */
    fun address(): dynamic
}
