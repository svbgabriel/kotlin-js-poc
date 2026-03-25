package io.github.svbgabriel.infrastructure.web.routes

import io.github.svbgabriel.infrastructure.web.WebApplication
import io.github.svbgabriel.infrastructure.web.controller.ContactController
import io.github.svbgabriel.infrastructure.web.openapi.OpenApiHelper
import io.github.svbgabriel.infrastructure.web.openapi.Operation
import io.github.svbgabriel.infrastructure.web.openapi.Parameter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.js.json

fun WebApplication.installContactRoutes() {
    val koinComponent = object : KoinComponent {
        val controller: ContactController by inject()
    }

    val contactSchema = OpenApiHelper.schema(
        type = "object",
        properties = mapOf(
            "name" to OpenApiHelper.schema(type = "string", example = "John Doe"),
            "nickname" to OpenApiHelper.schema(type = "string", example = "Johnny"),
            "email" to OpenApiHelper.schema(type = "string", format = "email", example = "john@example.com")
        ),
        required = listOf("name", "email")
    )

    route("/api/contacts") {
        get(
            openApi = Operation(
                summary = "List all contacts",
                tags = arrayOf("Contacts"),
                responses = json(
                    "200" to OpenApiHelper.response(
                        "A list of contacts",
                        schema = OpenApiHelper.schema(type = "array", items = contactSchema)
                    )
                )
            )
        ) { koinComponent.controller.findAll(this) }

        post(
            openApi = Operation(
                summary = "Create a new contact",
                tags = arrayOf("Contacts"),
                requestBody = OpenApiHelper.requestBody("Contact data", schema = contactSchema),
                responses = json(
                    "201" to OpenApiHelper.response(
                        "Contact created successfully",
                        schema = contactSchema
                    )
                )
            )
        ) { koinComponent.controller.create(this) }

        route("/:id") {
            get(
                openApi = Operation(
                    summary = "Get contact by ID",
                    tags = arrayOf("Contacts"),
                    parameters = arrayOf(
                        Parameter(
                            name = "id",
                            `in` = "path",
                            required = true,
                            schema = OpenApiHelper.schema(type = "string")
                        )
                    ),
                    responses = json(
                        "200" to OpenApiHelper.response("Contact found", schema = contactSchema),
                        "404" to OpenApiHelper.response("Contact not found")
                    )
                )
            ) { koinComponent.controller.findById(this) }

            delete(
                openApi = Operation(
                    summary = "Delete contact",
                    tags = arrayOf("Contacts"),
                    parameters = arrayOf(
                        Parameter(
                            name = "id",
                            `in` = "path",
                            required = true,
                            schema = OpenApiHelper.schema(type = "string")
                        )
                    ),
                    responses = json(
                        "204" to OpenApiHelper.response("Contact deleted"),
                        "404" to OpenApiHelper.response("Contact not found")
                    )
                )
            ) { koinComponent.controller.delete(this) }

            put(
                openApi = Operation(
                    summary = "Update contact",
                    tags = arrayOf("Contacts"),
                    parameters = arrayOf(
                        Parameter(
                            name = "id",
                            `in` = "path",
                            required = true,
                            schema = OpenApiHelper.schema(type = "string")
                        )
                    ),
                    requestBody = OpenApiHelper.requestBody("Updated contact data", schema = contactSchema),
                    responses = json(
                        "204" to OpenApiHelper.response("Contact updated"),
                        "404" to OpenApiHelper.response("Contact not found")
                    )
                )
            ) { koinComponent.controller.update(this) }
        }
    }
}
