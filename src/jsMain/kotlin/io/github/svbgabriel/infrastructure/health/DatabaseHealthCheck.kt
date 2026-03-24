package io.github.svbgabriel.infrastructure.health

import io.github.svbgabriel.infrastructure.database.mongooseVal

class DatabaseHealthCheck : HealthCheck {
    override val name: String = "database"

    override suspend fun check(): HealthCheckResult {
        val readyState = mongooseVal.connection.readyState
        // Mongoose readyState: 0 = disconnected, 1 = connected, 2 = connecting, 3 = disconnecting, 4 = unauthorized, 99 = uninitialized
        return if (readyState == 1) {
            HealthCheckResult(HealthStatus.UP, mapOf("database" to "MongoDB", "readyState" to readyState.toString()))
        } else {
            HealthCheckResult(HealthStatus.DOWN, mapOf("database" to "MongoDB", "readyState" to readyState.toString()))
        }
    }
}
