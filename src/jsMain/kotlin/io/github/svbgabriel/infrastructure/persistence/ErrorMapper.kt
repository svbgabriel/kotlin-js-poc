package io.github.svbgabriel.infrastructure.persistence

import io.github.svbgabriel.infrastructure.web.HttpStatus

/**
 * Maps a raw [Throwable] to a typed error response tuple when it originates from Mongoose.
 *
 * Returns a triple of (HTTP status, message, optional details) for known Mongoose error types,
 * or `null` if the error is not a recognized Mongoose error.
 *
 * - **MongoServerError (code 11000)** → 409 Conflict (duplicate key)
 * - **ValidationError** → 400 Bad Request with field-level details
 * - **Other Mongoose errors** → 500 Internal Server Error
 */
fun mapMongooseError(err: Throwable): Triple<HttpStatus, String, List<String>>? {
    val name = err.nameOrNull() ?: return null

    return when (name) {
        "MongoServerError" -> {
            val code = err.codeOrNull()
            if (code == 11000) {
                val message = err.messageOrNull()?.takeIf { it.isNotBlank() } ?: "Duplicate entry detected"
                Triple(HttpStatus.CONFLICT, message, emptyList())
            } else {
                val message = err.messageOrNull()?.takeIf { it.isNotBlank() } ?: "Database error"
                Triple(HttpStatus.INTERNAL_SERVER_ERROR, message, emptyList())
            }
        }
        "ValidationError" -> {
            val details = extractValidationDetails(err)
            val message = err.messageOrNull()?.takeIf { it.isNotBlank() } ?: "Validation failed"
            Triple(HttpStatus.BAD_REQUEST, message, details)
        }
        else -> {
            // Only handle known Mongoose error names; ignore generic JS errors
            val mongooseErrorNames = setOf("CastError", "StrictModeError", "ParallelSaveError", "VersionError", "DisconnectedError")
            if (name in mongooseErrorNames) {
                val message = err.messageOrNull()?.takeIf { it.isNotBlank() } ?: "Unknown database error"
                Triple(HttpStatus.INTERNAL_SERVER_ERROR, message, emptyList())
            } else {
                null
            }
        }
    }
}

/**
 * Safely reads the `name` property from a Throwable, treating it as a Mongoose error.
 */
private fun Throwable.nameOrNull(): String? {
    return try {
        val dynamic = this.asDynamic()
        if (dynamic.name != undefined) dynamic.name.toString() else null
    } catch (@Suppress("UNUSED_VARIABLE") e: Exception) {
        null
    }
}

/**
 * Safely reads the `code` property from a Throwable, treating it as a Mongoose error.
 */
private fun Throwable.codeOrNull(): Number? {
    return try {
        val dynamic = this.asDynamic()
        if (dynamic.code != undefined) dynamic.code as Number? else null
    } catch (@Suppress("UNUSED_VARIABLE") e: Exception) {
        null
    }
}

/**
 * Safely reads the `message` property from a Throwable, treating it as a Mongoose error.
 */
private fun Throwable.messageOrNull(): String? {
    return try {
        val dynamic = this.asDynamic()
        if (dynamic.message != undefined) dynamic.message.toString() else null
    } catch (@Suppress("UNUSED_VARIABLE") e: Exception) {
        null
    }
}

/**
 * Extracts field-level validation error details from a Mongoose ValidationError.
 *
 * Attempts to read the `errors` property from the Mongoose error object and extracts
 * the field name and message for each validation failure.
 */
private fun extractValidationDetails(error: Throwable): List<String> {
    val details = mutableListOf<String>()
    try {
        val errorsObj = error.asDynamic().errors ?: return emptyList()
        val keys = js("Object.keys(errorsObj)") as Array<String>
        for (key in keys) {
            val msg = errorsObj[key]?.message?.toString()
            if (msg != null && msg.isNotBlank()) {
                details.add("$key: $msg")
            }
        }
    } catch (@Suppress("UNUSED_VARIABLE") e: Exception) {
        // Fallback: return empty details if dynamic access fails
    }
    return details
}
