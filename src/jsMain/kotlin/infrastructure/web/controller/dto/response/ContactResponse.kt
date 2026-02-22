package io.github.svbgabriel.infrastructure.web.controller.dto.response

import io.github.svbgabriel.domain.model.Contact
import kotlinx.serialization.Serializable

@Serializable
data class ContactResponse(
    val id: String,
    val name: String,
    val nickname: String,
    val email: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    companion object {
        fun fromDomain(contact: Contact): ContactResponse {
            return ContactResponse(
                id = contact.id ?: throw IllegalStateException("Contact ID cannot be null in response"),
                name = contact.name,
                nickname = contact.nickname,
                email = contact.email,
                createdAt = contact.createdAt,
                updatedAt = contact.updatedAt
            )
        }
    }
}
