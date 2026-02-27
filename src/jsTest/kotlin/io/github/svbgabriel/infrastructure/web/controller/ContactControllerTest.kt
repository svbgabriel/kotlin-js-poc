package io.github.svbgabriel.infrastructure.web.controller

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.github.svbgabriel.MockRequest
import io.github.svbgabriel.MockResponse
import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.domain.service.ContactService
import io.github.svbgabriel.infrastructure.web.BadRequestException
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.request.UpdateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ContactResponse
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ErrorResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import kotlinx.serialization.json.encodeToDynamic
import kotlin.js.json

@OptIn(ExperimentalSerializationApi::class)
class ContactControllerTest : FunSpec({

    val jsonSerializer = Json { ignoreUnknownKeys = true }

    test("should retrieve all contacts successfully") {
        // Arrange
        val service = mock<ContactService>()
        val expectedContacts = arrayOf(
            Contact(id = "1", name = "John Doe", nickname = "Johnny", email = "john.doe@example.com")
        )
        everySuspend { service.findAll() } returns expectedContacts

        val controller = ContactController(service)
        val response = MockResponse()

        // Act
        controller.select(response)

        // Assert
        verifySuspend { service.findAll() }
        response.statusCode shouldBe HttpStatus.OK.statusCode
        val resultList = jsonSerializer.decodeFromDynamic<List<ContactResponse>>(response.jsonBody)
        resultList.size shouldBe 1
        resultList[0].name shouldBe "John Doe"
    }

    test("should handle failure when retrieving all contacts") {
        // Arrange
        val service = mock<ContactService>()
        everySuspend { service.findAll() } throws Exception("Database error")

        val controller = ContactController(service)
        val response = MockResponse()

        // Act & Assert
        shouldThrow<Exception> {
            controller.select(response)
        }
    }

    test("should retrieve a single contact by id successfully") {
        // Arrange
        val service = mock<ContactService>()
        val contactId = "1"
        val expectedContact = Contact(id = contactId, name = "Jane Doe", nickname = "Jane", email = "jane@example.com")
        everySuspend { service.findById(contactId) } returns expectedContact

        val controller = ContactController(service)
        val request = MockRequest(params = json("id" to contactId))
        val response = MockResponse()

        // Act
        controller.selectOne(request, response)

        // Assert
        verifySuspend { service.findById(contactId) }
        response.statusCode shouldBe HttpStatus.OK.statusCode
        val result = jsonSerializer.decodeFromDynamic<ContactResponse>(response.jsonBody)
        result.name shouldBe "Jane Doe"
    }

    test("should return not found when retrieving a non-existent contact by id") {
        // Arrange
        val service = mock<ContactService>()
        val contactId = "non-existent-id"
        everySuspend { service.findById(contactId) } returns null

        val controller = ContactController(service)
        val request = MockRequest(params = json("id" to contactId))
        val response = MockResponse()

        // Act
        controller.selectOne(request, response)

        // Assert
        verifySuspend { service.findById(contactId) }
        response.statusCode shouldBe HttpStatus.NOT_FOUND.statusCode
        val errorResult = jsonSerializer.decodeFromDynamic<ErrorResponse>(response.jsonBody)
        errorResult.message shouldBe "Contact not found"
    }

    test("should create a new contact successfully") {
        // Arrange
        val service = mock<ContactService>()
        val inputRequest = CreateContactRequest(
            name = "New Contact",
            nickname = "Newbie",
            email = "newbie@example.com"
        )
        val inputDynamic = jsonSerializer.encodeToDynamic(inputRequest)

        val expectedContact = Contact(
            id = "new-id",
            name = "New Contact",
            nickname = "Newbie",
            email = "newbie@example.com"
        )
        everySuspend { service.create(any()) } returns expectedContact

        val controller = ContactController(service)
        val request = MockRequest(body = inputDynamic)
        val response = MockResponse()

        // Act
        controller.insert(request, response)

        // Assert
        verifySuspend { service.create(any()) }
        response.statusCode shouldBe HttpStatus.CREATED.statusCode
        val result = jsonSerializer.decodeFromDynamic<ContactResponse>(response.jsonBody)
        result.name shouldBe "New Contact"
        result.id shouldBe "new-id"
    }

    test("should throw bad request exception when creating a contact with invalid data") {
        // Arrange
        val service = mock<ContactService>()
        val inputRequest = CreateContactRequest(
            name = "", // Invalid: blank
            nickname = "Newbie",
            email = "invalid-email" // Invalid: wrong format
        )
        val inputDynamic = jsonSerializer.encodeToDynamic(inputRequest)

        val controller = ContactController(service)
        val request = MockRequest(body = inputDynamic)
        val response = MockResponse()

        // Act
        val exception = shouldThrow<BadRequestException> {
            controller.insert(request, response)
        }

        // Assert
        exception.message shouldBe "name cannot be blank, Invalid e-mail format"
    }

    test("should update an existing contact successfully") {
        // Arrange
        val service = mock<ContactService>()
        val contactId = "1"
        val updateRequest = UpdateContactRequest(
            name = "Updated Contact",
            nickname = "Updated",
            email = "updated@example.com"
        )
        val inputDynamic = jsonSerializer.encodeToDynamic(updateRequest)

        everySuspend { service.update(contactId, any()) } returns true

        val controller = ContactController(service)
        val request = MockRequest(params = json("id" to contactId), body = inputDynamic)
        val response = MockResponse()

        // Act
        controller.update(request, response)

        // Assert
        verifySuspend { service.update(contactId, any()) }
        response.statusCode shouldBe HttpStatus.NO_CONTENT.statusCode
        (response.sentBody as Any?) shouldBe null
    }

    test("should return not found when updating a non-existent contact") {
        // Arrange
        val service = mock<ContactService>()
        val contactId = "non-existent-id"
        val updateRequest = UpdateContactRequest(
            name = "Updated Contact",
            nickname = "Updated",
            email = "updated@example.com"
        )
        val inputDynamic = jsonSerializer.encodeToDynamic(updateRequest)

        everySuspend { service.update(contactId, any()) } returns false

        val controller = ContactController(service)
        val request = MockRequest(params = json("id" to contactId), body = inputDynamic)
        val response = MockResponse()

        // Act
        controller.update(request, response)

        // Assert
        verifySuspend { service.update(contactId, any()) }
        response.statusCode shouldBe HttpStatus.NOT_FOUND.statusCode
        val errorResult = jsonSerializer.decodeFromDynamic<ErrorResponse>(response.jsonBody)
        errorResult.message shouldBe "Contact not found"
    }

    test("should delete a contact successfully") {
        // Arrange
        val service = mock<ContactService>()
        val contactId = "1"
        everySuspend { service.delete(contactId) } returns true

        val controller = ContactController(service)
        val request = MockRequest(params = json("id" to contactId))
        val response = MockResponse()

        // Act
        controller.delete(request, response)

        // Assert
        verifySuspend { service.delete(contactId) }
        response.statusCode shouldBe HttpStatus.NO_CONTENT.statusCode
        (response.sentBody as Any?) shouldBe null
    }

    test("should return not found when deleting a non-existent contact") {
        // Arrange
        val service = mock<ContactService>()
        val contactId = "non-existent-id"
        everySuspend { service.delete(contactId) } returns false

        val controller = ContactController(service)
        val request = MockRequest(params = json("id" to contactId))
        val response = MockResponse()

        // Act
        controller.delete(request, response)

        // Assert
        verifySuspend { service.delete(contactId) }
        response.statusCode shouldBe HttpStatus.NOT_FOUND.statusCode
        val errorResult = jsonSerializer.decodeFromDynamic<ErrorResponse>(response.jsonBody)
        errorResult.message shouldBe "Contact not found"
    }
})
