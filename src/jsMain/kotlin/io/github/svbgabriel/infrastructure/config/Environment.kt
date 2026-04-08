package io.github.svbgabriel.infrastructure.config

import io.github.svbgabriel.node.process

object Environment {
    fun get(key: String, defaultValue: String): String {
        return process.env[key] as? String ?: defaultValue
    }

    fun getOrNull(key: String): String? {
        return process.env[key] as? String
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return (process.env[key] as? String)?.toIntOrNull() ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return (process.env[key] as? String)?.toBooleanStrictOrNull() ?: defaultValue
    }

    fun set(key: String, value: String?) {
        process.env[key] = value
    }
}
