package infrastructure.web.plugin

import io.github.svbgabriel.infrastructure.health.DatabaseHealthCheck
import io.github.svbgabriel.infrastructure.health.installHealthCheck
import io.github.svbgabriel.infrastructure.web.WebApplication
import io.github.svbgabriel.infrastructure.web.installDefaultErrorHandlers
import io.github.svbgabriel.infrastructure.web.installProcessEventsHandler

fun WebApplication.installAppInfrastructure() {
    installProcessEventsHandler()
    installDefaultErrorHandlers()

    installHealthCheck {
        path = "/"
        register(DatabaseHealthCheck())
    }
}
