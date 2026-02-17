package io.github.svbgabriel.infrastructure.web.controller.dto

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

@Serializable
data class ContactResponse(
    val id: String,
    val name: String,
    val nickname: String,
    val email: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun Contact.toResponse(): ContactResponse {
    return ContactResponse(
        id = this.id ?: throw IllegalStateException("Contact ID cannot be null in response"),
        name = this.name,
        nickname = this.nickname,
        email = this.email,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
