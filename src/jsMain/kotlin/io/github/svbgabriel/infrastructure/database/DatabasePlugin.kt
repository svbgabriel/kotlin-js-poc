package io.github.svbgabriel.infrastructure.database

import io.github.svbgabriel.infrastructure.web.WebApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Extension to install and initialize database connection within the WebApplication.
 */
suspend fun WebApplication.installDatabase() {
    val databaseStarter = object : KoinComponent {
        val database: Database by inject()
    }
    databaseStarter.database.createConnection()
}
