package io.github.svbgabriel.infrastructure.web

import io.github.svbgabriel.infrastructure.externals.express.NextFunction
import io.github.svbgabriel.infrastructure.externals.express.Request
import io.github.svbgabriel.infrastructure.externals.express.Response
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

val errorMiddleware: (dynamic, Request, Response, NextFunction) -> Unit = { err, _, res, _ ->
    val statusCode = when (err) {
        is BadRequestException -> HttpStatus.BAD_REQUEST.statusCode
        is NotFoundException -> HttpStatus.NOT_FOUND.statusCode
        is SerializationException -> HttpStatus.BAD_REQUEST.statusCode
        else -> HttpStatus.INTERNAL_SERVER_ERROR.statusCode
    }

    val message = if (err is Throwable) err.message else err.toString()
    if (err is Throwable) {
        errorMiddlewareLogger.error(message ?: UNKNOWN_ERROR, err)
    } else {
        errorMiddlewareLogger.error(message ?: UNKNOWN_ERROR)
    }

    res.status(statusCode).json(json(
        "error" to true,
        "message" to (message ?: UNKNOWN_ERROR)
    ))
}
