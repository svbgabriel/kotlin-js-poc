package io.github.svbgabriel.di

import io.github.svbgabriel.infrastructure.web.WebApplication
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

/**
 * Extension to install Koin dependency injection into WebApplication.
 */
fun WebApplication.installDependencyInjectionModule(configure: KoinApplication.() -> Unit) {
    startKoin {
        configure()
    }
}
