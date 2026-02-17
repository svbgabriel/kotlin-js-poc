package io.github.svbgabriel.config

import io.github.svbgabriel.infrastructure.externals.javascript.encodeURIComponent
import io.github.svbgabriel.infrastructure.externals.node.process

object Configuration {
    val serverPort: Int = process.env.SERVER_PORT?.toIntOrNull() ?: 3000

    private val dbUser: String = process.env.DB_USER ?: "admin"
    private val dbPass: String = process.env.DB_PASS ?: "admin"
    private val dbHost: String = process.env.DB_HOST ?: "localhost"
    private val dbName: String = process.env.DB_NAME ?: "contacts"

    val dbUrl: String
        get() {
            val encodedUser = encodeURIComponent(dbUser)
            val encodedPass = encodeURIComponent(dbPass)
            return "mongodb://$encodedUser:$encodedPass@$dbHost/$dbName?authSource=admin"
        }
}
