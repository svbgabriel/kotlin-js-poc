package io.github.svbgabriel.domain.exception

/**
 * Thrown when a requested resource is not found.
 *
 * Maps to HTTP 404 Not Found at the web layer.
 */
class NotFoundException(message: String) : DomainException(message)
