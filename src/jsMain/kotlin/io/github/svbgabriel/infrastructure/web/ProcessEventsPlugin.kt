package io.github.svbgabriel.infrastructure.web

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.svbgabriel.node.process

private val logger = KotlinLogging.logger("ProcessEvents")

/**
 * Plugin to handle global process events like uncaughtException and unhandledRejection.
 */
fun WebApplication.installProcessEventsHandler() {
    process.on("uncaughtException") { err ->
        logger.error(err as? Throwable) { "Uncaught Exception: " }
        process.exit(1)
    }

    process.on("unhandledRejection") { reason ->
        logger.error { "Unhandled Rejection: $reason" }
    }
}
