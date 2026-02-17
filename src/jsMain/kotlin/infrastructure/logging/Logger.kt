package io.github.svbgabriel.infrastructure.logging

import kotlin.js.Date

interface Logger {
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
    fun debug(message: String)
}

class ConsoleLogger(private val context: String) : Logger {

    private fun formatMessage(level: String, message: String): String {
        val timestamp = Date().toISOString()
        return "[$timestamp] [$level] [$context] $message"
    }

    override fun info(message: String) {
        console.info(formatMessage("INFO", message))
    }

    override fun warn(message: String) {
        console.warn(formatMessage("WARN", message))
    }

    override fun error(message: String, throwable: Throwable?) {
        console.error(formatMessage("ERROR", message))
        throwable?.printStackTrace()
    }

    override fun debug(message: String) {
        // TODO: Implement debug logging with conditional check for environment
        console.log(formatMessage("DEBUG", message))
    }
}

object LoggerFactory {
    fun getLogger(context: String): Logger {
        return ConsoleLogger(context)
    }

    fun getLogger(clazz: Any): Logger {
        val name = clazz::class.simpleName ?: "Unknown"
        return ConsoleLogger(name)
    }
}
