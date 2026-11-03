package io.github.svbgabriel.infrastructure.web

import io.github.svbgabriel.domain.exception.ConflictException
import io.github.svbgabriel.domain.exception.NotFoundException
import io.github.svbgabriel.domain.exception.ValidationException
import io.github.svbgabriel.infrastructure.persistence.mapMongooseError
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.SerializationException

private val logger = KotlinLogging.logger("ErrorHandler")

fun WebApplication.installDefaultErrorHandlers() {
    val unknownError = "Unknown error"

    exception<ValidationException> { e ->
        logger.warn { "Handled error [${HttpStatus.BAD_REQUEST.statusCode}]: ${e.message}" }
        respondError(HttpStatus.BAD_REQUEST, e.message ?: unknownError, e.details)
    }

    exception<NotFoundException> { e ->
        logger.warn { "Handled error [${HttpStatus.NOT_FOUND.statusCode}]: ${e.message}" }
        respondError(HttpStatus.NOT_FOUND, e.message ?: unknownError)
    }

    exception<ConflictException> { e ->
        logger.warn { "Handled error [${HttpStatus.CONFLICT.statusCode}]: ${e.message}" }
        respondError(HttpStatus.CONFLICT, e.message ?: unknownError)
    }

    exception<SerializationException> { e ->
        logger.warn { "Handled error [${HttpStatus.BAD_REQUEST.statusCode}]: ${e.message}" }
        respondError(HttpStatus.BAD_REQUEST, e.message ?: unknownError)
    }

    exception<Throwable> { err ->
        val mapped = mapMongooseError(err)
        if (mapped != null) {
            val (status, message, details) = mapped
            if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                logger.error(err) { message }
            } else {
                logger.warn { "Handled error [${status.statusCode}]: $message" }
            }
            respondError(status, message, details)
        } else {
            logger.error(err) { unknownError }
            respondError(HttpStatus.INTERNAL_SERVER_ERROR, unknownError)
        }
    }
}
