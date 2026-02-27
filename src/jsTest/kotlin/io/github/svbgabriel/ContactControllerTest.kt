package io.github.svbgabriel

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.github.svbgabriel.infrastructure.web.controller.ContactController
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.domain.service.ContactService
import io.github.svbgabriel.infrastructure.web.BadRequestException
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ContactResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import kotlinx.serialization.json.encodeToDynamic

@OptIn(ExperimentalSerializationApi::class)
class ContactControllerTest : FunSpec({

    val jsonSerializer = Json { ignoreUnknownKeys = true }

    test("testSelectSuccess") {
        val service = mock<ContactService>()
        val expectedData = arrayOf(Contact(id = "1", name = "Contact 1", nickname = "C1", email = "test@example.com"))
        everySuspend { service.findAll() } returns expectedData

        val controller = ContactController(service)
        val res = MockResponse()

        controller.select(res)

        res.statusCode shouldBe HttpStatus.OK.statusCode
        val resultDynamic = res.jsonBody
        // resultDynamic is Array<dynamic> (plain JS objects)
        val resultList = jsonSerializer.decodeFromDynamic<List<ContactResponse>>(resultDynamic)

        resultList.size shouldBe 1
        resultList[0].name shouldBe "Contact 1"
    }

    test("testSelectFailure") {
        val service = mock<ContactService>()

        everySuspend { service.findAll() } throws Exception("Service error")

        val controller = ContactController(service)
        val res = MockResponse()

        shouldThrow<Exception> {
            controller.select(res)
        }
    }

    test("testInsertSuccess") {
        val service = mock<ContactService>()
        val inputRequest = CreateContactRequest(
            name = "New Contact",
            nickname = "NC",
            email = "new.contact@example.com"
        )
        val inputDynamic = jsonSerializer.encodeToDynamic(inputRequest)

        val expectedContact = Contact(
            id = "newId",
            name = "New Contact",
            nickname = "NC",
            email = "new.contact@example.com"
        )

        everySuspend { service.create(any()) } returns expectedContact

        val controller = ContactController(service)
        val req = MockRequest(body = inputDynamic)
        val res = MockResponse()

        controller.insert(req, res)

        res.statusCode shouldBe HttpStatus.CREATED.statusCode
        // We need to cast or decode the result properly
        // res.jsonBody is { result: ... }
        val resultDynamic = res.jsonBody
        val result = jsonSerializer.decodeFromDynamic<ContactResponse>(resultDynamic)

        result.id shouldBe "newId"
        result.name shouldBe "New Contact"
    }

    test("testInsertValidationFailure") {
        val service = mock<ContactService>()
        val inputRequest = CreateContactRequest(
            name = "", // Invalid name
            nickname = "NC",
            email = "invalid-email" // Invalid email
        )
        val inputDynamic = jsonSerializer.encodeToDynamic(inputRequest)

        val controller = ContactController(service)
        val req = MockRequest(body = inputDynamic)
        val res = MockResponse()

        val exception = shouldThrow<BadRequestException> {
            controller.insert(req, res)
        }
        exception.message shouldBe "name cannot be blank, Invalid e-mail format"
    }
})
