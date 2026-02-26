package io.github.svbgabriel.infrastructure.persistence

import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.domain.repository.ContactRepository
import kotlinx.coroutines.await
import kotlin.js.json

class ContactRepositoryImpl : ContactRepository {

    override suspend fun findAll(): Array<Contact> {
        val result = ContactModel.find(json()).lean().exec().await()
        return result.map { it.toDomain() }.toTypedArray()
    }

    override suspend fun findById(id: String): Contact? {
        val result = ContactModel.findById(id).lean().exec().await()
        return result?.toDomain()
    }

    override suspend fun create(contact: Contact): Contact {
        val newContact = contact.toInputEntity()

        val result = ContactModel.create(newContact).await()
        return result.toDomain()
    }

    override suspend fun update(id: String, contact: Contact): Boolean {
        val contactToUpdate = contact.toInputEntity()

        val result = ContactModel.updateOne(json("_id" to id), contactToUpdate).await()
        return result.matchedCount > 0
    }

    override suspend fun delete(id: String): Boolean {
        val result = ContactModel.deleteOne(json("_id" to id)).await()
        val count = result.deletedCount
        return count > 0
    }
}
