package io.github.svbgabriel.infrastructure.web.controller.validation

import io.github.svbgabriel.infrastructure.web.BadRequestException
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.request.UpdateContactRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class ContactValidatorTest : FunSpec({

    test("should validate correct CreateContactRequest successfully") {
        // Arrange
        val request = CreateContactRequest(
            name = "Valid Name",
            nickname = "ValidNick",
            email = "valid@example.com"
        )

        // Act & Assert (Should not throw any exception)
        ContactValidator.validate(request)
    }

    test("should throw BadRequestException for CreateContactRequest with blank fields") {
        // Arrange
        val request = CreateContactRequest(
            name = " ",
            nickname = "",
            email = "valid@example.com"
        )

        // Act
        val exception = shouldThrow<BadRequestException> {
            ContactValidator.validate(request)
        }

        // Assert
        exception.message shouldContain "name cannot be blank"
        exception.message shouldContain "nickname cannot be blank"
    }

    test("should throw BadRequestException for CreateContactRequest with invalid email format") {
        // Arrange
        val request = CreateContactRequest(
            name = "Valid",
            nickname = "ValidNick",
            email = "invalid-email"
        )

        // Act
        val exception = shouldThrow<BadRequestException> {
            ContactValidator.validate(request)
        }

        // Assert
        exception.message shouldContain "Invalid e-mail format"
    }

    test("should validate correct UpdateContactRequest successfully") {
        // Arrange
        val request = UpdateContactRequest(
            name = "Valid Name",
            nickname = "ValidNick",
            email = "valid@example.com"
        )

        // Act & Assert (Should not throw any exception)
        ContactValidator.validate(request)
    }

    test("should throw BadRequestException for UpdateContactRequest with blank fields and invalid email") {
        // Arrange
        val request = UpdateContactRequest(
            name = "",
            nickname = "ValidNick",
            email = "not_an_email"
        )

        // Act
        val exception = shouldThrow<BadRequestException> {
            ContactValidator.validate(request)
        }

        // Assert
        exception.message shouldContain "name cannot be blank"
        exception.message shouldContain "Invalid e-mail format"
    }
})
