package io.github.svbgabriel.infrastructure.web

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.svbgabriel.infrastructure.config.Environment
import io.github.svbgabriel.infrastructure.externals.node.process
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * Factory for creating WebApplication instances.
 */
object WebFactory {
    private val logger = KotlinLogging.logger("WebFactory")

    /**
     * Creates a [WebApplication] instance.
     *
     * @param T The type of the WebApplication to create.
     * @param factory A function that creates a WebApplication instance given a [CoroutineScope].
     * @param overridePort An optional port to override the default one from the environment.
     * @param onListen A callback invoked when the server starts listening.
     * @param configure A configuration block for the application instance.
     * @return The created [WebApplication] instance.
     */
    fun <T : WebApplication> create(
        factory: (CoroutineScope) -> T,
        overridePort: Int?,
        onListen: (WebServer) -> Unit = {},
        configure: suspend T.() -> Unit = {}
    ): T {
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        val app = factory(scope)

        scope.launch {
            app.applyDotenv()
            app.configure()
            val port = Environment.getInt("SERVER_PORT", 3000)
            val serverPort = overridePort ?: port
            val server = app.listen(serverPort) {
                logger.info { "Server started on port $serverPort" }
            }
            onListen(server)

            val shutdown: (dynamic, dynamic) -> Unit = { signal, _ ->
                logger.info { "Received $signal. Starting graceful shutdown..." }
                scope.launch {
                    try {
                        withTimeout(30000.milliseconds) { // 30 seconds timeout
                            server.shutdown()
                        }
                        logger.info { "Graceful shutdown completed. Exiting." }
                        process.exit(0)
                    } catch (e: Exception) {
                        logger.error(e) { "Error during graceful shutdown" }
                        process.exit(1)
                    }
                }
            }

            process.once("SIGTERM", shutdown)
            process.once("SIGINT", shutdown)
        }

        return app
    }
}

/**
 * Extension for creating a WebApplication using a DSL.
 * By default, it uses Express as the underlying engine.
 *
 * @param overridePort An optional port to override the default one from the environment.
 * @param factory A function that creates a [WebApplication] instance given a [CoroutineScope].
 * @param onListen A callback invoked when the server starts listening.
 * @param configure A configuration block for the application instance.
 * @return The created [WebApplication] instance.
 */
fun embeddedServer(
    overridePort: Int? = null,
    factory: (CoroutineScope) -> WebApplication = { scope -> ExpressWebApplication(scope = scope) },
    onListen: (WebServer) -> Unit = {},
    configure: suspend WebApplication.() -> Unit = {}
): WebApplication {
    return WebFactory.create(factory, overridePort, onListen, configure)
}
