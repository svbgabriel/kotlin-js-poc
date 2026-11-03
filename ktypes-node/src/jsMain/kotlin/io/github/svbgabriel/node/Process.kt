package io.github.svbgabriel.node

/**
 * External module for Node.js process.
 */
@JsModule("process")
@JsNonModule
external val process: NodeProcess

/**
 * Represents the Node.js process object.
 */
external interface NodeProcess {
    /**
     * An object containing the user environment.
     * Shortcut for `process.env`.
     *
     * Note: Uses `dynamic` because `process.env` is a plain JS object that doesn't support
     * method-based access (`.get()`/`.set()`). The `external interface` approach with operator
     * get/set compiles to method calls, which fail at runtime.
     */
    val env: dynamic

    /**
     * Instructs Node.js to terminate the process synchronously with an exit status of [code].
     */
    fun exit(code: Int = definedExternally)

    /**
     * Adds the [listener] function to the end of the listener array for the event named [event].
     */
    fun on(event: String, listener: (Any?) -> Unit)

    /**
     * Adds a one-time [listener] function for the event named [event].
     */
    fun once(event: String, listener: (Any?) -> Unit)
}
