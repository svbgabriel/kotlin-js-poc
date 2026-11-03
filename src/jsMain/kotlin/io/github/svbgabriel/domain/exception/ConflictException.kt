package io.github.svbgabriel.domain.exception

/**
 * Thrown when a resource conflict occurs, such as a duplicate unique key.
 *
 * Maps to HTTP 409 Conflict at the web layer.
 */
class ConflictException(message: String) : DomainException(message)
