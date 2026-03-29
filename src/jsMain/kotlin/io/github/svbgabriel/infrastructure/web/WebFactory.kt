package io.github.svbgabriel.infrastructure.web

import io.github.svbgabriel.infrastructure.externals.node.process
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
 */
fun embeddedServer(
    overridePort: Int? = null,
    factory: (CoroutineScope) -> WebApplication = { scope -> ExpressWebApplication(scope = scope) },
    onListen: (WebServer) -> Unit = {},
    configure: suspend WebApplication.() -> Unit = {}
): WebApplication {
    return WebFactory.create(factory, overridePort, onListen, configure)
}
