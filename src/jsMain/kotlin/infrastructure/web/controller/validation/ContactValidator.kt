package io.github.svbgabriel.infrastructure.web.controller.validation

import io.github.svbgabriel.infrastructure.web.controller.dto.CreateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.UpdateContactRequest
import io.github.svbgabriel.infrastructure.web.BadRequestException

object ContactValidator {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

    fun validate(request: CreateContactRequest) {
        val errors = mutableListOf<String>()

        if (request.name.isBlank()) {
            errors.add("Name cannot be blank")
        }
        if (request.nickname.isBlank()) {
            errors.add("Nickname cannot be blank")
        }
        if (request.email.isBlank()) {
            errors.add("Email cannot be blank")
        } else if (!emailRegex.matches(request.email)) {
            errors.add("Invalid email format")
        }

        if (errors.isNotEmpty()) {
            throw BadRequestException(errors.joinToString(", "))
        }
    }

    fun validate(request: UpdateContactRequest) {
        val errors = mutableListOf<String>()

        if (request.name.isBlank()) {
            errors.add("Name cannot be blank")
        }
        if (request.nickname.isBlank()) {
            errors.add("Nickname cannot be blank")
        }
        if (request.email.isBlank()) {
            errors.add("Email cannot be blank")
        } else if (!emailRegex.matches(request.email)) {
            errors.add("Invalid email format")
        }

        if (errors.isNotEmpty()) {
            throw BadRequestException(errors.joinToString(", "))
        }
    }
}
