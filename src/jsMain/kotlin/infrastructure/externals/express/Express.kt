package io.github.svbgabriel.infrastructure.externals.express

import kotlinx.js.JsPlainObject

@JsModule("express")
@JsNonModule
external val expressMiddleware: ExpressMiddleware

@JsModule("express")
@JsNonModule
external fun expressApplication(): ExpressApplication

typealias NextFunction = (dynamic) -> Unit

external interface ExpressApplication {
    fun use(middleware: dynamic)
    fun use(path: String, middleware: dynamic)
    fun listen(port: Int, callback: () -> Unit)
    fun route(path: String): Route

    fun get(path: String, handler: (Request, Response, NextFunction) -> Unit)
    fun post(path: String, handler: (Request, Response, NextFunction) -> Unit)
    fun put(path: String, handler: (Request, Response, NextFunction) -> Unit)
    fun delete(path: String, handler: (Request, Response, NextFunction) -> Unit)
}

external interface ExpressMiddleware {
    fun json(): (Request, Response, NextFunction) -> Unit
    fun urlencoded(options: ExpressUrlEncodedOptions): (Request, Response, NextFunction) -> Unit
}

external interface Route {
    fun get(handler: (Request, Response, NextFunction) -> Unit): Route
    fun post(handler: (Request, Response, NextFunction) -> Unit): Route
    fun put(handler: (Request, Response, NextFunction) -> Unit): Route
    fun delete(handler: (Request, Response, NextFunction) -> Unit): Route
}

external interface Request {
    val params: dynamic
    val body: dynamic
    val query: dynamic
    val headers: dynamic
}

external interface Response {
    fun status(code: Int): Response
    fun json(body: dynamic): Response
    fun send(body: dynamic?): Response
    fun set(field: String, vararg value: String): Response
}

@JsPlainObject
external interface ExpressUrlEncodedOptions {
    val extended: Boolean?
}
