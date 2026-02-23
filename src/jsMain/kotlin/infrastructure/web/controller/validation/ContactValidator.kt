package io.github.svbgabriel.infrastructure.web.controller.validation

import io.github.svbgabriel.infrastructure.web.BadRequestException
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.request.UpdateContactRequest
import io.konform.validation.Validation
import io.konform.validation.constraints.notBlank
import io.konform.validation.constraints.pattern

object ContactValidator {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

    private val validateCreateContactRequest = Validation {
        CreateContactRequest::name {
            notBlank() hint "name cannot be blank"
        }
        CreateContactRequest::nickname {
            notBlank() hint "nickname cannot be blank"
        }
        CreateContactRequest::email {
            notBlank() hint "email cannot be blank"
            pattern(emailRegex) hint "Invalid e-mail format"
        }
    }

    private val validateUpdateContactRequest = Validation {
        UpdateContactRequest::name {
            notBlank() hint "name cannot be blank"
        }
        UpdateContactRequest::nickname {
            notBlank() hint "nickname cannot be blank"
        }
        UpdateContactRequest::email {
            notBlank() hint "email cannot be blank"
            pattern(emailRegex) hint "Invalid e-mail format"
        }
    }

    private fun <T> validate(validation: Validation<T>, payload: T) {
        val errors = validation(payload)
            .takeUnless { it.isValid }
            ?.errors
            ?.map { it.message }
            ?: emptyList()

        if (errors.isNotEmpty()) {
            throw BadRequestException(errors.joinToString(", "))
        }
    }

    fun validate(request: CreateContactRequest) {
        validate(validateCreateContactRequest, request)
    }

    fun validate(request: UpdateContactRequest) {
        validate(validateUpdateContactRequest, request)
    }
}
