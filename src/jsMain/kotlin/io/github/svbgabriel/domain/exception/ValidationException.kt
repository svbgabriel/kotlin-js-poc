package io.github.svbgabriel.domain.exception

/**
 * Thrown when input validation fails.
 *
 * Maps to HTTP 400 Bad Request at the web layer. Supports a list of
 * granular field-level error details for structured error responses.
 */
class ValidationException(message: String, val details: List<String> = emptyList()) : DomainException(message)
