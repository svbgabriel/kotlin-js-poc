package io.github.svbgabriel.infrastructure.health

import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.infrastructure.web.WebApplication
import io.github.svbgabriel.infrastructure.web.respond

class HealthConfig {
    var path: String = "/health"
    private val checks = mutableListOf<HealthCheck>()

    fun register(check: HealthCheck) {
        checks.add(check)
    }

    internal suspend fun execute(): HealthResponse {
        val results = checks.associate { it.name to it.check() }
        val overallStatus = if (results.values.all { it.status == HealthStatus.UP }) {
            HealthStatus.UP
        } else {
            HealthStatus.DOWN
        }
        return HealthResponse(overallStatus, results)
    }
}

fun WebApplication.installHealthCheck(configure: HealthConfig.() -> Unit = {}) {
    val config = HealthConfig().apply(configure)
    
    route(config.path) {
        get {
            val response = config.execute()
            val status = if (response.status == HealthStatus.UP) HttpStatus.OK else HttpStatus.INTERNAL_SERVER_ERROR
            respond(status, response)
        }
    }
}
