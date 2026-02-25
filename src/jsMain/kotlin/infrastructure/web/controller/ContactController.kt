package io.github.svbgabriel.infrastructure.web.controller

import io.github.svbgabriel.infrastructure.web.controller.validation.ContactValidator
import io.github.svbgabriel.infrastructure.externals.express.Request
import io.github.svbgabriel.infrastructure.externals.express.Response
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.domain.service.ContactService
import io.github.svbgabriel.infrastructure.externals.express.ExpressExtensions.receive
import io.github.svbgabriel.infrastructure.externals.express.ExpressExtensions.sendJson
import io.github.svbgabriel.infrastructure.externals.express.ExpressExtensions.sendNoContent
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.request.UpdateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ContactResponse
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ErrorResponse

class ContactController(private val service: ContactService) {

    suspend fun select(res: Response) {
        val result = service.findAll()
            .map { ContactResponse.fromDomain(it) }
            .toTypedArray()

        res.sendJson(HttpStatus.OK, result)
    }

    suspend fun selectOne(req: Request, res: Response) {
        val id = req.params["id"].toString()
        val result = service.findById(id)

        if (result != null) {
            val response = ContactResponse.fromDomain(result)
            res.sendJson(HttpStatus.OK, response)
        } else {
            res.sendJson(HttpStatus.NOT_FOUND, ErrorResponse("Contact not found"))
        }
    }

    suspend fun insert(req: Request, res: Response) {
        val body = req.receive<CreateContactRequest>()
        ContactValidator.validate(body)
        val contactInput = body.toDomain()

        val result = service.create(contactInput)
        val response = ContactResponse.fromDomain(result)
        res.sendJson(HttpStatus.CREATED, response)
    }

    suspend fun update(req: Request, res: Response) {
        val id = req.params["id"].toString()
        val body = req.receive<UpdateContactRequest>()
        ContactValidator.validate(body)
        val contactInput = body.toDomain(id)

        val updated = service.update(id, contactInput)
        if (updated) {
            res.sendNoContent()
        } else {
            res.sendJson(HttpStatus.NOT_FOUND, ErrorResponse("Contact not found"))
        }
    }

    suspend fun delete(req: Request, res: Response) {
        val id = req.params["id"].toString()
        val result = service.delete(id)

        if (result) {
            res.sendNoContent()
        } else {
            res.sendJson(HttpStatus.NOT_FOUND, ErrorResponse("Contact not found"))
        }
    }
}
