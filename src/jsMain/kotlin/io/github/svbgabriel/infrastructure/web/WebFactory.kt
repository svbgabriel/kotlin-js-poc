package io.github.svbgabriel.infrastructure.web

import io.github.svbgabriel.infrastructure.config.Environment
import io.github.svbgabriel.infrastructure.logging.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Factory for creating WebApplication instances.
 */
object WebFactory {
    private val logger = LoggerFactory.getLogger("WebFactory")

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
                logger.info("Server started on port $serverPort")
            }
            onListen(server)
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
