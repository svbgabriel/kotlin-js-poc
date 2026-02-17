package io.github.svbgabriel.domain.service

import io.github.svbgabriel.domain.model.Contact

interface ContactService {
    suspend fun findAll(): Array<Contact>
    suspend fun findById(id: String): Contact?
    suspend fun create(contact: Contact): Contact
    suspend fun update(id: String, contact: Contact): Boolean
    suspend fun delete(id: String): Boolean
}
