package io.github.svbgabriel.infrastructure.web.controller.dto.request

import io.github.svbgabriel.domain.model.Contact
import kotlinx.serialization.Serializable

@Serializable
data class CreateContactRequest(
    val name: String,
    val nickname: String,
    val email: String
) {
    fun toDomain(): Contact = Contact(
        name = name,
        nickname = nickname,
        email = email
    )
}
