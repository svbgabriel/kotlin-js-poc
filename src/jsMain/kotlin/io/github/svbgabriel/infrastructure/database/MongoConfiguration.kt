package io.github.svbgabriel.infrastructure.database

import io.github.svbgabriel.infrastructure.config.Environment
import io.github.svbgabriel.infrastructure.externals.javascript.encodeURIComponent

object MongoConfiguration {

    private val dbUser: String
        get() = Environment.get("DB_USER", "admin")
    private val dbPass: String
        get() = Environment.get("DB_PASS", "admin")
    private val dbHost: String
        get() = Environment.get("DB_HOST", "localhost")
    private val dbPort: String
        get() = Environment.get("DB_PORT", "27017")
    private val dbName: String
        get() = Environment.get("DB_NAME", "contacts")
    private val dbAuthSource: String
        get() = Environment.get("DB_AUTH_SOURCE", "admin")

    val dbUrl: String
        get() {
            val encodedUser = encodeURIComponent(dbUser)
            val encodedPass = encodeURIComponent(dbPass)
            return "mongodb://$encodedUser:$encodedPass@$dbHost:$dbPort/$dbName?authSource=$dbAuthSource"
        }
}
