package io.github.svbgabriel.infrastructure.web.controller

import io.github.svbgabriel.infrastructure.web.controller.validation.ContactValidator
import io.github.svbgabriel.infrastructure.externals.express.Request
import io.github.svbgabriel.infrastructure.externals.express.Response
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.domain.service.ContactService
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.request.UpdateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ContactResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import kotlinx.serialization.json.encodeToDynamic
import kotlin.js.json

@OptIn(ExperimentalSerializationApi::class)
class ContactController(private val service: ContactService) {

    private val jsonSerializer = Json

    suspend fun select(res: Response) {
        val result = service.findAll()
            .map { jsonSerializer.encodeToDynamic(ContactResponse.fromDomain(it)) }
            .toTypedArray()

        res.status(HttpStatus.OK.statusCode).json(json("result" to result))
    }

    suspend fun selectOne(req: Request, res: Response) {
        val id = req.params["id"].toString()
        val result = service.findById(id)
        if (result != null) {
            val responseDynamic = jsonSerializer.encodeToDynamic(ContactResponse.fromDomain(result))
            res.status(HttpStatus.OK.statusCode).json(json("result" to responseDynamic))
        } else {
            res.status(HttpStatus.NOT_FOUND.statusCode).json(json("result" to HttpStatus.NOT_FOUND.description))
        }
    }

    suspend fun insert(req: Request, res: Response) {
        val request = jsonSerializer.decodeFromDynamic<CreateContactRequest>(req.body)
        ContactValidator.validate(request)
        val contactInput = request.toDomain()

        val result = service.create(contactInput)
        val responseDynamic = jsonSerializer.encodeToDynamic(ContactResponse.fromDomain(result))
        res.status(HttpStatus.CREATED.statusCode).json(json("result" to responseDynamic))
    }

    suspend fun update(req: Request, res: Response) {
        val id = req.params["id"].toString()
        val request = jsonSerializer.decodeFromDynamic<UpdateContactRequest>(req.body)
        ContactValidator.validate(request)
        val contactInput = request.toDomain(id)

        val updated = service.update(id, contactInput)
        if (updated) {
            res.status(HttpStatus.NO_CONTENT.statusCode).send(body = null)
        } else {
            res.status(HttpStatus.NOT_FOUND.statusCode).json(json("result" to HttpStatus.NOT_FOUND.description))
        }
    }

    suspend fun delete(req: Request, res: Response) {
        val id = req.params["id"].toString()
        val result = service.delete(id)
        if (result) {
            res.status(HttpStatus.NO_CONTENT.statusCode).send(body = null)
        } else {
            res.status(HttpStatus.NOT_FOUND.statusCode).json(json("result" to HttpStatus.NOT_FOUND.description))
        }
    }
}
