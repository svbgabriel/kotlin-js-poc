package io.github.svbgabriel.domain.application.service

import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.domain.repository.ContactRepository
import io.github.svbgabriel.domain.service.ContactService

class ContactServiceImpl(private val repository: ContactRepository) : ContactService {
    override suspend fun findAll(): Array<Contact> = repository.findAll()
    override suspend fun findById(id: String): Contact? = repository.findById(id)
    override suspend fun create(contact: Contact): Contact = repository.create(contact)
    override suspend fun update(id: String, contact: Contact) = repository.update(id, contact)
    override suspend fun delete(id: String): Boolean = repository.delete(id)
}
