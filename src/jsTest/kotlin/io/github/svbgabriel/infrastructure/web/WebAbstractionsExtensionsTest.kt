package io.github.svbgabriel.infrastructure.web

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.matcher.any
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.konform.validation.Validation
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.KSerializer

class WebAbstractionsExtensionsTest : FunSpec({

    test("body validation should use custom error message when provided") {
        val context = mock<WebContext>()
        val validation = Validation<CreateContactRequest> {
            CreateContactRequest::name { minLength(10) }
        }
        val request = CreateContactRequest(name = "Short", nickname = "Nick", email = "test@test.com")
        val customMessage = "Custom error message"

        everySuspend { context.receive(any<KSerializer<CreateContactRequest>>()) } returns request

        val exception = shouldThrow<BadRequestException> {
            context.body(validation, customMessage)
        }

        exception.message shouldBe customMessage
    }

    test("body validation should use default error message when custom is not provided") {
        val context = mock<WebContext>()
        val validation = Validation<CreateContactRequest> {
            CreateContactRequest::name { minLength(10) }
        }
        val request = CreateContactRequest(name = "Short", nickname = "Nick", email = "test@test.com")

        everySuspend { context.receive(any<KSerializer<CreateContactRequest>>()) } returns request

        val exception = shouldThrow<BadRequestException> {
            context.body(validation)
        }

        exception.message shouldBe ".name: must have at least 10 characters"
    }

    test("param validation should use custom error message for missing parameter") {
        val context = mock<WebContext>()
        val customMessage = "Required ID is missing"
        
        everySuspend { context.params } returns emptyMap()

        val exception = shouldThrow<BadRequestException> {
            context.param("id", customMessage) { it }
        }

        exception.message shouldBe customMessage
    }

    test("param validation should use custom error message for invalid transformation") {
        val context = mock<WebContext>()
        val customMessage = "ID must be numeric"
        
        everySuspend { context.params } returns mapOf("id" to "abc")

        val exception = shouldThrow<BadRequestException> {
            context.param("id", customMessage) { it.toIntOrNull() }
        }

        exception.message shouldBe customMessage
    }

    test("query validation should use custom error message for invalid transformation") {
        val context = mock<WebContext>()
        val customMessage = "Page must be numeric"
        
        everySuspend { context.query } returns mapOf("page" to "abc")

        val exception = shouldThrow<BadRequestException> {
            context.query("page", customMessage) { it.toIntOrNull() }
        }

        exception.message shouldBe customMessage
    }
    
    test("param validation should use default error message when custom is not provided") {
        val context = mock<WebContext>()
        
        everySuspend { context.params } returns emptyMap()

        val exception = shouldThrow<BadRequestException> {
            context.param("id") { it }
        }

        exception.message shouldBe "Parameter 'id' is required"
        
        everySuspend { context.params } returns mapOf("id" to "abc")
        val exception2 = shouldThrow<BadRequestException> {
            context.param("id") { null }
        }
        exception2.message shouldBe "Invalid value for parameter 'id'"
    }

    test("header validation should use custom error message when provided") {
        val context = mock<WebContext>()
        val customMessage = "Custom header error"

        everySuspend { context.headers } returns mapOf("X-Custom" to listOf("invalid"))

        val exception = shouldThrow<BadRequestException> {
            context.header("X-Custom", customMessage) { null }
        }

        exception.message shouldBe customMessage
    }

    test("header validation should use default error message when custom is not provided") {
        val context = mock<WebContext>()

        everySuspend { context.headers } returns mapOf("X-Custom" to listOf("invalid"))

        val exception = shouldThrow<BadRequestException> {
            context.header("X-Custom") { null }
        }

        exception.message shouldBe "Invalid value for header 'X-Custom'"
    }

    test("header validation should return null when header is missing") {
        val context = mock<WebContext>()

        everySuspend { context.headers } returns emptyMap()

        val result = context.header("X-Custom") { it }

        result shouldBe null
    }

    test("header validation should return transformed value when present") {
        val context = mock<WebContext>()

        everySuspend { context.headers } returns mapOf("X-Custom" to listOf("123"))

        val result = context.header("X-Custom") { it.toIntOrNull() }

        result shouldBe 123
    }
})
