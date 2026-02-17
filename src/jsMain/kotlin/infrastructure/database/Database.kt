package io.github.svbgabriel.infrastructure.database

import io.github.svbgabriel.config.Configuration
import io.github.svbgabriel.infrastructure.logging.LoggerFactory

class Database {

    private val dbURL = Configuration.dbUrl
    private val logger = LoggerFactory.getLogger(this)

    fun createConnection() {
        // Log sensitive URL? Maybe only mask password?
        // logger.info("Connecting to database: $dbURL") // Maybe redact password
        mongooseVal.connect(dbURL)
        setupListeners()
    }

    private fun setupListeners() {
        val dbConnection = mongooseVal.connection
        dbConnection.on("connected") {
            logger.info("Mongoose connected successfully")
        }
        dbConnection.on("error") { error ->
            logger.error("Mongoose connection error: $error")
        }
    }
}
