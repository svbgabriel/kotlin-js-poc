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
     */
    val env: EnvironmentVariables

    /**
     * Instructs Node.js to terminate the process synchronously with an exit status of [code].
     */
    fun exit(code: Int = definedExternally)

    /**
     * Adds the [listener] function to the end of the listeners array for the event named [event].
     */
    fun on(event: String, listener: (dynamic, dynamic) -> Unit)

    /**
     * Adds a one-time [listener] function for the event named [event].
     */
    fun once(event: String, listener: (dynamic, dynamic) -> Unit)
}

/**
 * Represents environment variables used in the application.
 */
external interface EnvironmentVariables {
    var DB_USER: String?
    var DB_PASS: String?
    var DB_HOST: String?
    var DB_PORT: String?
    var DB_NAME: String?
    var DB_AUTH_SOURCE: String?

    var SERVER_PORT: String?
}
