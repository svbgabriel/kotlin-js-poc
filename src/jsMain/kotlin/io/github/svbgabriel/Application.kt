package io.github.svbgabriel

import io.github.svbgabriel.di.appModule
import io.github.svbgabriel.di.installDependencyInjectionModule
import io.github.svbgabriel.infrastructure.database.installDatabase
import io.github.svbgabriel.infrastructure.web.embeddedServer
import io.github.svbgabriel.infrastructure.web.openapi.OpenApiInfo
import io.github.svbgabriel.infrastructure.web.openapi.installOpenApi
import io.github.svbgabriel.infrastructure.web.plugin.installAppInfrastructure
import io.github.svbgabriel.infrastructure.web.plugin.installDotenv
import io.github.svbgabriel.infrastructure.web.routes.installContactRoutes

fun main() {
    embeddedServer {
        installDotenv { quiet = true }

        installDependencyInjectionModule {
            modules(appModule)
        }

        installDatabase()

        installAppInfrastructure()

        installOpenApi(
            info = OpenApiInfo(
                title = "Contacts API",
                version = "1.0.0",
                description = "API for managing contacts"
            )
        )

        installContactRoutes()
    }
}
