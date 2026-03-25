package io.github.svbgabriel.domain.model

data class Contact(
    val id: String?,
    val name: String,
    val nickname: String,
    val email: String,
    val createdAt: String?,
    val updatedAt: String?
)
