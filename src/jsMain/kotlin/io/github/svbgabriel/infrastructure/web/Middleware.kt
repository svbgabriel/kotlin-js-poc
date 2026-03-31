package io.github.svbgabriel.infrastructure.web

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.SerializationException

private val logger = KotlinLogging.logger("ErrorHandler")

fun WebApplication.installDefaultErrorHandlers() {
    val unknownError = "Unknown error"

    exception<BadRequestException> { e ->
        logger.warn { "Handled error [${HttpStatus.BAD_REQUEST.statusCode}]: ${e.message}" }
        respondError(HttpStatus.BAD_REQUEST, e.message ?: unknownError)
    }

    exception<NotFoundException> { e ->
        logger.warn { "Handled error [${HttpStatus.NOT_FOUND.statusCode}]: ${e.message}" }
        respondError(HttpStatus.NOT_FOUND, e.message ?: unknownError)
    }

    exception<SerializationException> { e ->
        logger.warn { "Handled error [${HttpStatus.BAD_REQUEST.statusCode}]: ${e.message}" }
        respondError(HttpStatus.BAD_REQUEST, e.message ?: unknownError)
    }

    exception<Throwable> { err ->
        val statusCode = when (err.asDynamic().name) {
            "MongoServerError" if err.asDynamic().code == 11000 -> HttpStatus.CONFLICT.statusCode
            "ValidationError" -> HttpStatus.BAD_REQUEST.statusCode
            else -> HttpStatus.INTERNAL_SERVER_ERROR.statusCode
        }

        val message: String = when {
            err.asDynamic().message != undefined -> err.asDynamic().message.toString()
            else -> err.message ?: unknownError
        }

        if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.statusCode) {
            logger.error(err) { message }
        } else {
            logger.warn { "Handled error [$statusCode]: $message" }
        }

        respondError(HttpStatus.fromStatusCode(statusCode), message)
    }
}
