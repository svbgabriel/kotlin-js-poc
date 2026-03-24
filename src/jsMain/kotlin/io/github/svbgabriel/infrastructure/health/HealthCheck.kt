package io.github.svbgabriel.infrastructure.health

import kotlinx.serialization.Serializable

@Serializable
enum class HealthStatus {
    UP, DOWN
}

@Serializable
data class HealthCheckResult(
    val status: HealthStatus,
    val details: Map<String, String> = emptyMap()
)

@Serializable
data class HealthResponse(
    val status: HealthStatus,
    val checks: Map<String, HealthCheckResult>
)

interface HealthCheck {
    val name: String
    suspend fun check(): HealthCheckResult
}
