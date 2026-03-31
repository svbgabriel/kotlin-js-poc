package io.github.svbgabriel.infrastructure.externals.node

@JsModule("process")
@JsNonModule
external val process: NodeProcess

external interface NodeProcess {
    val env: EnvironmentVariables
    fun exit(code: Int = definedExternally)
    fun on(event: String, listener: (dynamic, dynamic) -> Unit)
    fun once(event: String, listener: (dynamic, dynamic) -> Unit)
}

external interface EnvironmentVariables {
    var DB_USER: String?
    var DB_PASS: String?
    var DB_HOST: String?
    var DB_PORT: String?
    var DB_NAME: String?
    var DB_AUTH_SOURCE: String?

    var SERVER_PORT: String?
}
