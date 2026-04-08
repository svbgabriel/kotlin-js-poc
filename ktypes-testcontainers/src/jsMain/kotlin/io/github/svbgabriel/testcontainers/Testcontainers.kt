@file:JsModule("testcontainers")
@file:JsNonModule

package io.github.svbgabriel.testcontainers

import kotlin.js.Promise

/**
 * External class for GenericContainer in testcontainers.
 * @param image The name of the Docker image to use.
 */
@JsName("GenericContainer")
external class GenericContainer(image: String) {
    /**
     * Specifies the ports that should be exposed.
     * @param ports The ports to expose.
     * @return This container instance.
     */
    fun withExposedPorts(vararg ports: Int): GenericContainer

    /**
     * Sets environment variables for the container.
     * @param env An object containing environment variable key-value pairs.
     * @return This container instance.
     */
    fun withEnvironment(env: dynamic): GenericContainer

    /**
     * Starts the container.
     * @return A [Promise] that resolves to a [StartedTestContainer].
     */
    fun start(): Promise<StartedTestContainer>
}

/**
 * Represents a started test container.
 */
external interface StartedTestContainer {
    /**
     * Returns the mapped port for the specified container port.
     * @param port The port in the container.
     * @return The mapped port on the host.
     */
    fun getMappedPort(port: Int): Int

    /**
     * Returns the host where the container is running.
     * @return The host.
     */
    fun getHost(): String

    /**
     * Stops the container.
     * @return A [Promise] that resolves when the container is stopped.
     */
    fun stop(): Promise<Unit>
}
