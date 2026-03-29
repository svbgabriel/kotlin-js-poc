package io.github.svbgabriel.infrastructure.web.plugin

import io.github.svbgabriel.infrastructure.web.DotenvConfiguration
import io.github.svbgabriel.infrastructure.web.WebApplication

/**
 * Plugin to install and enable Dotenv support in the application.
 * @param configuration Optional configuration for dotenv.
 */
fun WebApplication.installDotenv(configuration: (DotenvConfiguration.() -> Unit)? = null) {
    dotenv.enabled = true
    configuration?.invoke(dotenv)
    applyDotenv()
}
