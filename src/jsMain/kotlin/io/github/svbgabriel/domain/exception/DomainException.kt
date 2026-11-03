package io.github.svbgabriel.domain.exception

/**
 * Base exception for all domain-level errors.
 *
 * Serves as the root of the domain exception hierarchy, allowing centralized
 * handling of business-logic failures at the web layer.
 */
open class DomainException(message: String) : RuntimeException(message)
