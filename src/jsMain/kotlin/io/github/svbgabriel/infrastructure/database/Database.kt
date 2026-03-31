package io.github.svbgabriel.infrastructure.database

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.await

class Database {

    private val dbURL = MongoConfiguration.dbUrl
    private val logger = KotlinLogging.logger {}

    suspend fun createConnection() {
        // Log sensitive URL? Maybe only mask password?
        // logger.info("Connecting to database: $dbURL") // Maybe redact password
        setupListeners()
        mongooseVal.connect(dbURL).await()
    }

    private fun setupListeners() {
        val dbConnection = mongooseVal.connection
        dbConnection.on("connected") {
            logger.info { "Mongoose connected successfully" }
        }
        dbConnection.on("error") { error ->
            logger.error { "Mongoose connection error: $error" }
        }
    }
}
