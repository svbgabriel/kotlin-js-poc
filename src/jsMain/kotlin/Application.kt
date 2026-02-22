package io.github.svbgabriel

import io.github.svbgabriel.config.Configuration
import io.github.svbgabriel.infrastructure.web.controller.ContactController
import io.github.svbgabriel.di.appModule
import io.github.svbgabriel.infrastructure.database.Database
import io.github.svbgabriel.infrastructure.externals.express.ExpressUrlEncodedOptions
import io.github.svbgabriel.infrastructure.externals.express.expressApplication
import io.github.svbgabriel.infrastructure.externals.express.expressMiddleware
import io.github.svbgabriel.infrastructure.logging.LoggerFactory
import io.github.svbgabriel.infrastructure.web.routes.configureRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class App : KoinComponent {
    val app = expressApplication()
    private val database: Database by inject()
    private val controller: ContactController by inject()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        middleware()
        database.createConnection()
        app.configureRoutes(controller, scope)
    }

    private fun middleware() {
        app.use(expressMiddleware.json())
        app.use(expressMiddleware.urlencoded(options = ExpressUrlEncodedOptions(extended = true)))
    }
}

fun main() {
    startKoin {
        modules(appModule)
    }
    val serverPort = Configuration.serverPort
    val logger = LoggerFactory.getLogger("Application")

    val application = App()
    application.app.listen(serverPort) {
        logger.info("Server started on port $serverPort")
    }
}
