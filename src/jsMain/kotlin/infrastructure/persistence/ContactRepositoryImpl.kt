package io.github.svbgabriel.infrastructure.persistence

import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.domain.repository.ContactRepository
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic
import kotlin.js.json

@OptIn(ExperimentalSerializationApi::class)
class ContactRepositoryImpl : ContactRepository {

    private val jsonSerializer = Json { ignoreUnknownKeys = true }

    override suspend fun findAll(): Array<Contact> {
        val result = ContactModel.find(json()).lean().exec().await()
        return result.map { it.toDomain() }.toTypedArray()
    }

    override suspend fun findById(id: String): Contact? {
        val result = ContactModel.findById(id).lean().exec().await()
        return result?.toDomain()
    }

    override suspend fun create(contact: Contact): Contact {
        val contactDynamic = jsonSerializer.encodeToDynamic(contact)
        // Ensure _id is removed if null/undefined, so Mongoose generates it
        if (contactDynamic._id == null) {
            js("delete contactDynamic._id")
        }

        val result = ContactModel.create(contactDynamic).await()
        return result.toDomain()
    }

    override suspend fun update(id: String, contact: Contact): Boolean {
        val contactDynamic = jsonSerializer.encodeToDynamic(contact)
        // Ensure _id is removed if null/undefined
        if (contactDynamic._id == null) {
            js("delete contactDynamic._id")
        }

        val result = ContactModel.updateOne(json("_id" to id), contactDynamic).await()
        return result.matchedCount > 0
    }

    override suspend fun delete(id: String): Boolean {
        val result = ContactModel.deleteOne(json("_id" to id)).await()
        val count = result.deletedCount
        return count > 0
    }
}
