package io.github.svbgabriel.infrastructure.externals.express

import kotlinx.js.JsPlainObject
import kotlin.js.Json

@JsModule("express")
@JsNonModule
external val expressMiddleware: ExpressMiddleware

@JsModule("express")
@JsNonModule
external fun expressApplication(): ExpressApplication

typealias NextFunction = (Any?) -> Unit
typealias RequestHandler = (Request, Response, NextFunction) -> Unit
typealias ErrorRequestHandler = (Any, Request, Response, NextFunction) -> Unit

external interface ExpressApplication {
    fun use(middleware: RequestHandler)
    fun use(errorHandler: ErrorRequestHandler)
    fun use(path: String, middleware: RequestHandler)
    fun listen(port: Int, callback: () -> Unit)
    fun route(path: String): Route

    fun get(path: String, handler: RequestHandler)
    fun post(path: String, handler: RequestHandler)
    fun put(path: String, handler: RequestHandler)
    fun delete(path: String, handler: RequestHandler)
    fun patch(path: String, handler: RequestHandler)
}

external interface ExpressMiddleware {
    fun json(): RequestHandler
    fun urlencoded(options: ExpressUrlEncodedOptions): RequestHandler
}

external interface Route {
    fun get(handler: RequestHandler): Route
    fun post(handler: RequestHandler): Route
    fun put(handler: RequestHandler): Route
    fun delete(handler: RequestHandler): Route
    fun patch(handler: RequestHandler): Route
}

external interface Request {
    val params: Json
    val body: Any?
    val query: Json
    val headers: Json
}

external interface Response {
    fun status(code: Int): Response
    fun json(body: Any?): Response
    fun send(body: Any?): Response
    fun set(field: String, vararg value: String): Response
}

@JsPlainObject
external interface ExpressUrlEncodedOptions {
    val extended: Boolean?
}
