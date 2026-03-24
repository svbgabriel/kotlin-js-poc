package io.github.svbgabriel.infrastructure.web.routes

import io.github.svbgabriel.infrastructure.web.WebApplication
import io.github.svbgabriel.infrastructure.web.controller.ContactController
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun WebApplication.installContactRoutes() {
    val koinComponent = object : KoinComponent {
        val controller: ContactController by inject()
    }

    route("/api/contacts") {
        get { koinComponent.controller.findAll(this) }
        post { koinComponent.controller.create(this) }

        route("/:id") {
            get { koinComponent.controller.findById(this) }
            delete { koinComponent.controller.delete(this) }
            put { koinComponent.controller.update(this) }
        }
    }
}
