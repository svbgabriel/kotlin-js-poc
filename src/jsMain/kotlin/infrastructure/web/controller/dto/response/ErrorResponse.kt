package io.github.svbgabriel.infrastructure.web.controller.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
)
