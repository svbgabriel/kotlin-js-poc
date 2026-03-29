package io.github.svbgabriel.infrastructure.externals.dotenv

import kotlinx.js.JsPlainObject

@JsModule("dotenv")
@JsNonModule
external object Dotenv {
    fun config(options: DotenvOptions = definedExternally): DotenvResult
}

@JsPlainObject
external interface DotenvOptions {
    val path: String?
    val encoding: String?
    val debug: Boolean?
    val override: Boolean?
    val quiet: Boolean?
}

external interface DotenvResult {
    val error: Any?
    val parsed: dynamic
}
