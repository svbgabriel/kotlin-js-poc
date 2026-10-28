package io.github.svbgabriel.infrastructure.database

import io.github.svbgabriel.infrastructure.config.Environment
import io.github.svbgabriel.javascript.encodeURIComponent

object MongoConfiguration {

    private val dbUser: String
        get() = Environment.getOrNull("DB_USER") ?: throw IllegalStateException("DB_USER environment variable is not set")
    private val dbPass: String
        get() = Environment.getOrNull("DB_PASS") ?: throw IllegalStateException("DB_PASS environment variable is not set")
    private val dbHost: String
        get() = Environment.getOrNull("DB_HOST") ?: "localhost"
    private val dbPort: String
        get() = Environment.getOrNull("DB_PORT") ?: "27017"
    private val dbName: String
        get() = Environment.getOrNull("DB_NAME") ?: "contacts"
    private val dbAuthSource: String
        get() = Environment.getOrNull("DB_AUTH_SOURCE") ?: "admin"

    val dbUrl: String
        get() {
            val encodedUser = encodeURIComponent(dbUser)
            val encodedPass = encodeURIComponent(dbPass)
            return "mongodb://$encodedUser:$encodedPass@$dbHost:$dbPort/$dbName?authSource=$dbAuthSource"
        }
}
