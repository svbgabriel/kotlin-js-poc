package io.github.svbgabriel.infrastructure.web.controller.dto.request

import io.github.svbgabriel.domain.model.Contact
import kotlinx.serialization.Serializable

@Serializable
data class UpdateContactRequest(
    val name: String,
    val nickname: String,
    val email: String
) {
    fun toDomain(id: String): Contact = Contact(
        id = id,
        name = name,
        nickname = nickname,
        email = email
    )
}
