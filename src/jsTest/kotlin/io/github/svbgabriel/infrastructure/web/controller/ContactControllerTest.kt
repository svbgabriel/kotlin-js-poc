package io.github.svbgabriel.infrastructure.web.controller

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.domain.service.ContactService
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.infrastructure.web.WebContext
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.request.UpdateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ContactResponse
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ErrorResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class ContactControllerTest : FunSpec({

    val jsonSerializer = Json { ignoreUnknownKeys = true }

    test("should retrieve all contacts successfully") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        val expectedContacts = arrayOf(
            Contact(id = "1", name = "John Doe", nickname = "Johnny", email = "john.doe@example.com")
        )
        everySuspend { service.findAll() } returns expectedContacts
        everySuspend { context.respond(any<HttpStatus>(), any<List<ContactResponse>>(), any<KSerializer<List<ContactResponse>>>()) } returns Unit

        val controller = ContactController(service)

        // Act
        controller.findAll(context)

        // Assert
        verifySuspend { service.findAll() }
        verifySuspend { context.respond(HttpStatus.OK, any<List<ContactResponse>>(), any()) }
    }

    test("should handle failure when retrieving all contacts") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        everySuspend { service.findAll() } throws Exception("Database error")

        val controller = ContactController(service)

        // Act & Assert
        shouldThrow<Exception> {
            controller.findAll(context)
        }
    }

    test("should retrieve a single contact by id successfully") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        val contactId = "1"
        val expectedContact = Contact(id = contactId, name = "Jane Doe", nickname = "Jane", email = "jane@example.com")
        everySuspend { service.findById(contactId) } returns expectedContact
        everySuspend { context.params } returns mapOf("id" to contactId)
        everySuspend { context.respond(any<HttpStatus>(), any<ContactResponse>(), any<KSerializer<ContactResponse>>()) } returns Unit

        val controller = ContactController(service)

        // Act
        controller.findById(context)

        // Assert
        verifySuspend { service.findById(contactId) }
        verifySuspend { context.respond(HttpStatus.OK, any<ContactResponse>(), any()) }
    }

    test("should return not found when retrieving a non-existent contact by id") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        val contactId = "non-existent-id"
        everySuspend { service.findById(contactId) } returns null
        everySuspend { context.params } returns mapOf("id" to contactId)
        everySuspend { context.respond(any<HttpStatus>(), any<ErrorResponse>(), any<KSerializer<ErrorResponse>>()) } returns Unit

        val controller = ContactController(service)

        // Act
        controller.findById(context)

        // Assert
        verifySuspend { service.findById(contactId) }
        verifySuspend { context.respond(HttpStatus.NOT_FOUND, any<ErrorResponse>(), any()) }
    }

    test("should create a new contact successfully") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        val inputRequest = CreateContactRequest(
            name = "New Contact",
            nickname = "Newbie",
            email = "newbie@example.com"
        )

        val expectedContact = Contact(
            id = "new-id",
            name = "New Contact",
            nickname = "Newbie",
            email = "newbie@example.com"
        )
        everySuspend { service.create(any()) } returns expectedContact
        everySuspend { context.receive(any<KSerializer<CreateContactRequest>>()) } returns inputRequest
        everySuspend { context.respond(any<HttpStatus>(), any<ContactResponse>(), any<KSerializer<ContactResponse>>()) } returns Unit

        val controller = ContactController(service)

        // Act
        controller.create(context)

        // Assert
        verifySuspend { service.create(any()) }
        verifySuspend { context.respond(HttpStatus.CREATED, any<ContactResponse>(), any()) }
    }

    test("should update an existing contact successfully") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        val contactId = "1"
        val updateRequest = UpdateContactRequest(
            name = "Updated Contact",
            nickname = "Updated",
            email = "updated@example.com"
        )

        everySuspend { service.update(contactId, any()) } returns true
        everySuspend { context.params } returns mapOf("id" to contactId)
        everySuspend { context.receive(any<KSerializer<UpdateContactRequest>>()) } returns updateRequest
        everySuspend { context.respondStatus(any()) } returns Unit

        val controller = ContactController(service)

        // Act
        controller.update(context)

        // Assert
        verifySuspend { service.update(contactId, any()) }
        verifySuspend { context.respondStatus(HttpStatus.NO_CONTENT) }
    }

    test("should return not found when updating a non-existent contact") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        val contactId = "non-existent-id"
        val updateRequest = UpdateContactRequest(
            name = "Updated Contact",
            nickname = "Updated",
            email = "updated@example.com"
        )

        everySuspend { service.update(contactId, any()) } returns false
        everySuspend { context.params } returns mapOf("id" to contactId)
        everySuspend { context.receive(any<KSerializer<UpdateContactRequest>>()) } returns updateRequest
        everySuspend { context.respond(any<HttpStatus>(), any<ErrorResponse>(), any<KSerializer<ErrorResponse>>()) } returns Unit

        val controller = ContactController(service)

        // Act
        controller.update(context)

        // Assert
        verifySuspend { service.update(contactId, any()) }
        verifySuspend { context.respond(HttpStatus.NOT_FOUND, any<ErrorResponse>(), any()) }
    }

    test("should delete a contact successfully") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        val contactId = "1"
        everySuspend { service.delete(contactId) } returns true
        everySuspend { context.params } returns mapOf("id" to contactId)
        everySuspend { context.respondStatus(any()) } returns Unit

        val controller = ContactController(service)

        // Act
        controller.delete(context)

        // Assert
        verifySuspend { service.delete(contactId) }
        verifySuspend { context.respondStatus(HttpStatus.NO_CONTENT) }
    }

    test("should return not found when deleting a non-existent contact") {
        // Arrange
        val service = mock<ContactService>()
        val context = mock<WebContext>()
        val contactId = "non-existent-id"
        everySuspend { service.delete(contactId) } returns false
        everySuspend { context.params } returns mapOf("id" to contactId)
        everySuspend { context.respond(any<HttpStatus>(), any<ErrorResponse>(), any<KSerializer<ErrorResponse>>()) } returns Unit

        val controller = ContactController(service)

        // Act
        controller.delete(context)

        // Assert
        verifySuspend { service.delete(contactId) }
        verifySuspend { context.respond(HttpStatus.NOT_FOUND, any<ErrorResponse>(), any()) }
    }
})
