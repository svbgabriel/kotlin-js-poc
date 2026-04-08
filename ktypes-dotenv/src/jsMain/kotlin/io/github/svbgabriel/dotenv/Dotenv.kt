package io.github.svbgabriel.dotenv

import kotlinx.js.JsPlainObject

/**
 * External module for dotenv.
 * Loads environment variables from a .env file into process.env.
 */
@JsModule("dotenv")
@JsNonModule
external object Dotenv {
    /**
     * Loads .env file contents into process.env.
     * @param options The options for loading the .env file.
     * @return A [DotenvResult] object.
     */
    fun config(options: DotenvOptions = definedExternally): DotenvResult
}

/**
 * Options for dotenv configuration.
 */
@JsPlainObject
external interface DotenvOptions {
    /**
     * Path to .env file.
     */
    val path: String?

    /**
     * Encoding of .env file.
     */
    val encoding: String?

    /**
     * Turn on logging to help debug why certain keys or values are not being set as you expect.
     */
    val debug: Boolean?

    /**
     * Override any environment variables that have already been set on your machine with values from your .env file.
     */
    val override: Boolean?

    /**
     * Suppress all error messages and warnings.
     */
    val quiet: Boolean?
}

/**
 * Result of the dotenv configuration.
 */
external interface DotenvResult {
    /**
     * Error if the configuration failed.
     */
    val error: Any?

    /**
     * Parsed environment variables.
     */
    val parsed: dynamic
}
