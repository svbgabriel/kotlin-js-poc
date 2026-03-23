package io.github.svbgabriel.infrastructure.web

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
        port: Int,
        onListen: (WebServer) -> Unit = {},
        configure: suspend T.() -> Unit = {}
    ): T {
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        val app = factory(scope)
        
        scope.launch {
            app.configure()
            val server = app.listen(port) {
                logger.info("Server started on port $port")
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
    port: Int,
    factory: (CoroutineScope) -> WebApplication = { scope -> ExpressWebApplication(scope = scope) },
    onListen: (WebServer) -> Unit = {},
    configure: suspend WebApplication.() -> Unit = {}
): WebApplication {
    return WebFactory.create(factory, port, onListen, configure)
}
