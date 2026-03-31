package io.github.svbgabriel.infrastructure.web.controller

import io.github.svbgabriel.domain.ports.service.ContactService
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.infrastructure.web.WebContext
import io.github.svbgabriel.infrastructure.web.body
import io.github.svbgabriel.infrastructure.web.param
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ContactResponse
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ErrorResponse
import io.github.svbgabriel.infrastructure.web.controller.validation.ContactValidator
import io.github.svbgabriel.infrastructure.web.respond

class ContactController(private val service: ContactService) {

    suspend fun findAll(context: WebContext) {
        val result = service.findAll()
            .map { ContactResponse.fromDomain(it) }
        context.respond(HttpStatus.OK, result)
    }

    suspend fun findById(context: WebContext) {
        val id = context.param("id") { it.takeIf { it.isNotBlank() } }
        val result = service.findById(id)
        if (result != null) {
            context.respond(HttpStatus.OK, ContactResponse.fromDomain(result))
        } else {
            context.respond(HttpStatus.NOT_FOUND, ErrorResponse(CONTACT_NOT_FOUND))
        }
    }

    suspend fun create(context: WebContext) {
        val body = context.body(ContactValidator.validateCreateContactRequest)
        val contactInput = body.toDomain()
        val result = service.create(contactInput)
        context.respond(HttpStatus.CREATED, ContactResponse.fromDomain(result))
    }

    suspend fun update(context: WebContext) {
        val id = context.param("id") { it.takeIf { it.isNotBlank() } }
        val body = context.body(ContactValidator.validateUpdateContactRequest)
        val contactInput = body.toDomain(id)
        val updated = service.update(id, contactInput)
        if (updated) {
            context.respondStatus(HttpStatus.NO_CONTENT)
        } else {
            context.respond(HttpStatus.NOT_FOUND, ErrorResponse(CONTACT_NOT_FOUND))
        }
    }

    suspend fun delete(context: WebContext) {
        val id = context.param("id") { it.takeIf { it.isNotBlank() } }
        val deleted = service.delete(id)
        if (deleted) {
            context.respondStatus(HttpStatus.NO_CONTENT)
        } else {
            context.respond(HttpStatus.NOT_FOUND, ErrorResponse(CONTACT_NOT_FOUND))
        }
    }

    companion object {
        const val CONTACT_NOT_FOUND = "Contact not found"
        const val ID_IS_REQUIRED = "ID is required"
    }
}
