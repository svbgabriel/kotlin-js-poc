@file:JsModule("testcontainers")
@file:JsNonModule

package io.github.svbgabriel.infrastructure.externals.testcontainers

import kotlin.js.Promise

@JsName("GenericContainer")
external class GenericContainer(image: String) {
    fun withExposedPorts(vararg ports: Int): GenericContainer
    fun withEnvironment(env: dynamic): GenericContainer
    fun start(): Promise<StartedTestContainer>
}

external interface StartedTestContainer {
    fun getMappedPort(port: Int): Int
    fun getHost(): String
    fun stop(): Promise<Unit>
}
