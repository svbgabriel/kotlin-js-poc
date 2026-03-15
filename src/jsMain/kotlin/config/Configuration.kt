package io.github.svbgabriel.config

import io.github.svbgabriel.infrastructure.externals.javascript.encodeURIComponent
import io.github.svbgabriel.infrastructure.externals.node.process

object Configuration {
    val serverPort: Int
        get() = process.env.SERVER_PORT?.toIntOrNull() ?: 3000

    private val dbUser: String
        get() = process.env.DB_USER ?: "admin"
    private val dbPass: String
        get() = process.env.DB_PASS ?: "admin"
    private val dbHost: String
        get() = process.env.DB_HOST ?: "localhost"
    private val dbPort: String
        get() = process.env.DB_PORT ?: "27017"
    private val dbName: String
        get() = process.env.DB_NAME ?: "contacts"
    private val dbAuthSource: String
        get() = process.env.DB_AUTH_SOURCE ?: "admin"

    val dbUrl: String
        get() {
            val encodedUser = encodeURIComponent(dbUser)
            val encodedPass = encodeURIComponent(dbPass)
            return "mongodb://$encodedUser:$encodedPass@$dbHost:$dbPort/$dbName?authSource=$dbAuthSource"
        }
}
