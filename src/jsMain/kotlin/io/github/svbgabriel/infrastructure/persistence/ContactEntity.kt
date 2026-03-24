package io.github.svbgabriel.infrastructure.persistence

import io.github.svbgabriel.domain.model.Contact
import kotlinx.js.JsPlainObject
import kotlin.js.Date

@JsPlainObject
external interface ContactEntity {
    val _id: Any?
    val name: String
    val nickname: String
    val email: String
    val createdAt: Any?
    val updatedAt: Any?
}

fun ContactEntity.toDomain(): Contact {
    val idStr = this._id.toString()
    val createdAtStr = if (this.createdAt is Date) {
        (this.createdAt as Date).toISOString()
    } else {
        this.createdAt?.toString()
    }

    val updatedAtStr = if (this.updatedAt is Date) {
        (this.updatedAt as Date).toISOString()
    } else {
        this.updatedAt?.toString()
    }

    return Contact(
        id = idStr,
        name = this.name,
        nickname = this.nickname,
        email = this.email,
        createdAt = createdAtStr,
        updatedAt = updatedAtStr
    )
}

fun Contact.toInputEntity(): ContactEntity {
    return ContactEntity(
        name = this.name,
        nickname = this.nickname,
        email = this.email,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
