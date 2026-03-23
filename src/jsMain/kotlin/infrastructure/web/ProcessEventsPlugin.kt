package io.github.svbgabriel.infrastructure.web

import io.github.svbgabriel.infrastructure.externals.node.process
import io.github.svbgabriel.infrastructure.logging.LoggerFactory

/**
 * Plugin to handle global process events like uncaughtException and unhandledRejection.
 */
fun WebApplication.installProcessEventsHandler() {
    val logger = LoggerFactory.getLogger("ProcessEvents")

    process.on("uncaughtException") { err, _ ->
        logger.error("Uncaught Exception: ", err)
        process.exit(1)
    }

    process.on("unhandledRejection") { reason, promise ->
        logger.error("Unhandled Rejection at promise: ", promise)
        logger.error("Reason: ", reason)
    }
}
