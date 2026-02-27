package io.github.svbgabriel.domain.application.service

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.domain.repository.ContactRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ContactServiceImplTest : FunSpec({

    test("should retrieve all contacts from repository") {
        // Arrange
        val repository = mock<ContactRepository>()
        val expectedContacts = arrayOf(
            Contact(id = "1", name = "Test", nickname = "Testy", email = "test@example.com")
        )
        everySuspend { repository.findAll() } returns expectedContacts
        val service = ContactServiceImpl(repository)

        // Act
        val result = service.findAll()

        // Assert
        verifySuspend { repository.findAll() }
        result shouldBe expectedContacts
    }

    test("should retrieve a contact by id from repository") {
        // Arrange
        val repository = mock<ContactRepository>()
        val contactId = "1"
        val expectedContact = Contact(id = contactId, name = "Test", nickname = "Testy", email = "test@example.com")
        everySuspend { repository.findById(contactId) } returns expectedContact
        val service = ContactServiceImpl(repository)

        // Act
        val result = service.findById(contactId)

        // Assert
        verifySuspend { repository.findById(contactId) }
        result shouldBe expectedContact
    }

    test("should return null when contact is not found by id in repository") {
        // Arrange
        val repository = mock<ContactRepository>()
        val contactId = "non-existent"
        everySuspend { repository.findById(contactId) } returns null
        val service = ContactServiceImpl(repository)

        // Act
        val result = service.findById(contactId)

        // Assert
        verifySuspend { repository.findById(contactId) }
        result shouldBe null
    }

    test("should create a contact using repository") {
        // Arrange
        val repository = mock<ContactRepository>()
        val newContact = Contact(name = "New", nickname = "Newbie", email = "new@example.com")
        val expectedContact = Contact(id = "new-id", name = "New", nickname = "Newbie", email = "new@example.com")
        everySuspend { repository.create(newContact) } returns expectedContact
        val service = ContactServiceImpl(repository)

        // Act
        val result = service.create(newContact)

        // Assert
        verifySuspend { repository.create(newContact) }
        result shouldBe expectedContact
    }

    test("should update a contact using repository") {
        // Arrange
        val repository = mock<ContactRepository>()
        val contactId = "1"
        val updateContact = Contact(name = "Updated", nickname = "Up", email = "up@example.com")
        everySuspend { repository.update(contactId, updateContact) } returns true
        val service = ContactServiceImpl(repository)

        // Act
        val result = service.update(contactId, updateContact)

        // Assert
        verifySuspend { repository.update(contactId, updateContact) }
        result shouldBe true
    }

    test("should return false when trying to update a non-existent contact") {
        // Arrange
        val repository = mock<ContactRepository>()
        val contactId = "non-existent"
        val updateContact = Contact(name = "Updated", nickname = "Up", email = "up@example.com")
        everySuspend { repository.update(contactId, updateContact) } returns false
        val service = ContactServiceImpl(repository)

        // Act
        val result = service.update(contactId, updateContact)

        // Assert
        verifySuspend { repository.update(contactId, updateContact) }
        result shouldBe false
    }

    test("should delete a contact using repository") {
        // Arrange
        val repository = mock<ContactRepository>()
        val contactId = "1"
        everySuspend { repository.delete(contactId) } returns true
        val service = ContactServiceImpl(repository)

        // Act
        val result = service.delete(contactId)

        // Assert
        verifySuspend { repository.delete(contactId) }
        result shouldBe true
    }

    test("should return false when trying to delete a non-existent contact") {
        // Arrange
        val repository = mock<ContactRepository>()
        val contactId = "non-existent"
        everySuspend { repository.delete(contactId) } returns false
        val service = ContactServiceImpl(repository)

        // Act
        val result = service.delete(contactId)

        // Assert
        verifySuspend { repository.delete(contactId) }
        result shouldBe false
    }
})
