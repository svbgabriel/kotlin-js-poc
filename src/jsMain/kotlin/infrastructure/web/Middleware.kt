package io.github.svbgabriel.infrastructure.web

import io.github.svbgabriel.infrastructure.externals.express.NextFunction
import io.github.svbgabriel.infrastructure.externals.express.Request
import io.github.svbgabriel.infrastructure.externals.express.Response
import io.github.svbgabriel.infrastructure.externals.express.ErrorRequestHandler
import io.github.svbgabriel.infrastructure.logging.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlin.js.json

fun asyncHandler(scope: CoroutineScope, block: suspend (Request, Response) -> Unit): (Request, Response, NextFunction) -> Unit {
    return { req, res, next ->
        scope.launch {
            try {
                block(req, res)
            } catch (e: Throwable) {
                next(e)
            }
        }
    }
}

val errorMiddlewareLogger = LoggerFactory.getLogger("ErrorMiddleware")

private const val UNKNOWN_ERROR = "Unknown error"

val errorMiddleware: ErrorRequestHandler = { err, _, res, _ ->
    val statusCode = when {
        err is BadRequestException -> HttpStatus.BAD_REQUEST.statusCode
        err is NotFoundException -> HttpStatus.NOT_FOUND.statusCode
        err is SerializationException -> HttpStatus.BAD_REQUEST.statusCode
        err.asDynamic().name == "MongoServerError" && err.asDynamic().code == 11000 -> HttpStatus.CONFLICT.statusCode
        err.asDynamic().name == "ValidationError" -> HttpStatus.BAD_REQUEST.statusCode
        else -> HttpStatus.INTERNAL_SERVER_ERROR.statusCode
    }

    val message: String = when {
        err is Throwable -> err.message ?: UNKNOWN_ERROR
        err.asDynamic().message != undefined -> err.asDynamic().message.toString() // To enable to catch native JS errors
        else -> err.toString()
    }

    if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.statusCode) {
        if (err is Throwable) {
            errorMiddlewareLogger.error(message, err)
        } else {
            errorMiddlewareLogger.error(message)
        }
    } else {
        errorMiddlewareLogger.warn("Handled error [${statusCode}]: $message")
    }

    res.status(statusCode).json(json(
        "error" to true,
        "message" to message
    ))
}
