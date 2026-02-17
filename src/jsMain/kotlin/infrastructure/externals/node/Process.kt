package io.github.svbgabriel.infrastructure.externals.node

@JsModule("process")
@JsNonModule
external val process: NodeProcess

external interface NodeProcess {
    val env: EnvironmentVariables
    fun exit(code: Int = definedExternally)
}

external interface EnvironmentVariables {
    val DB_USER: String?
    val DB_PASS: String?
    val DB_HOST: String?
    val DB_NAME: String?

    val SERVER_PORT: String?
}
