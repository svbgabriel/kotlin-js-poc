package io.github.svbgabriel

import io.github.svbgabriel.config.Configuration
import io.github.svbgabriel.di.appModule
import io.github.svbgabriel.di.installDependencyInjectionModule
import io.github.svbgabriel.infrastructure.database.installDatabase
import io.github.svbgabriel.infrastructure.web.embeddedServer
import io.github.svbgabriel.infrastructure.web.plugin.installAppInfrastructure
import io.github.svbgabriel.infrastructure.web.routes.installContactRoutes

fun main() {
    val serverPort = Configuration.serverPort

    embeddedServer(port = serverPort) {
        installDependencyInjectionModule {
            modules(appModule)
        }

        installDatabase()

        installAppInfrastructure()

        installContactRoutes()
    }
}
