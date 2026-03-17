package io.github.svbgabriel.e2e

import io.github.svbgabriel.infrastructure.externals.express.ExpressExtensions.jsonSerializer
import io.github.svbgabriel.infrastructure.externals.javascript.fetch
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.github.svbgabriel.infrastructure.web.controller.dto.request.CreateContactRequest
import io.github.svbgabriel.infrastructure.web.controller.dto.response.ContactResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromDynamic
import kotlin.js.json

@OptIn(ExperimentalSerializationApi::class)
class ContactE2ETest : E2ETestBase() {
    init {
        test("should perform full CRUD on contacts") {
        // 1. List (empty)
        val initialListResponse = fetch("${baseUrl()}/api/contacts").await()
        initialListResponse.status shouldBe HttpStatus.OK.statusCode
        val initialList = jsonSerializer.decodeFromDynamic<Array<ContactResponse>>(initialListResponse.json().await())
        initialList.toList() shouldHaveSize 0

        // 2. Create
        val newContact = CreateContactRequest(
            name = "E2E User",
            nickname = "e2e",
            email = "e2e@example.com"
        )
        val createResponse = fetch(
            "${baseUrl()}/api/contacts",
            json(
                "method" to "POST",
                "headers" to json("Content-Type" to "application/json"),
                "body" to jsonSerializer.encodeToString(newContact)
            )
        ).await()
        
        createResponse.status shouldBe HttpStatus.CREATED.statusCode
        val created = jsonSerializer.decodeFromDynamic<ContactResponse>(createResponse.json().await())
        created.name shouldBe "E2E User"
        val contactId = created.id

        // 3. Get One
        val getOneResponse = fetch("${baseUrl()}/api/contacts/$contactId").await()
        getOneResponse.status shouldBe HttpStatus.OK.statusCode
        val found = jsonSerializer.decodeFromDynamic<ContactResponse>(getOneResponse.json().await())
        found.id shouldBe contactId
        found.name shouldBe "E2E User"

        // 4. Update
        val updateResponse = fetch(
            "${baseUrl()}/api/contacts/$contactId",
            json(
                "method" to "PUT",
                "headers" to json("Content-Type" to "application/json"),
                "body" to jsonSerializer.encodeToString(newContact.copy(name = "Updated E2E"))
            )
        ).await()
        updateResponse.status shouldBe HttpStatus.NO_CONTENT.statusCode

        // 5. Verify Update
        val verifyUpdateResponse = fetch("${baseUrl()}/api/contacts/$contactId").await()
        val updatedContact = jsonSerializer.decodeFromDynamic<ContactResponse>(verifyUpdateResponse.json().await())
        updatedContact.name shouldBe "Updated E2E"

        // 6. Delete
        val deleteResponse = fetch(
            "${baseUrl()}/api/contacts/$contactId",
            json("method" to "DELETE")
        ).await()
        deleteResponse.status shouldBe HttpStatus.NO_CONTENT.statusCode

        // 7. Final check
        val finalCheckResponse = fetch("${baseUrl()}/api/contacts/$contactId").await()
        finalCheckResponse.status shouldBe HttpStatus.NOT_FOUND.statusCode
    }

    test("should return 400 when validation fails") {
        val invalidContact = CreateContactRequest(
            name = "", // Blank
            nickname = "e2e",
            email = "invalid-email"
        )
        val response = fetch(
            "${baseUrl()}/api/contacts",
            json(
                "method" to "POST",
                "headers" to json("Content-Type" to "application/json"),
                "body" to jsonSerializer.encodeToString(invalidContact)
            )
        ).await()
        
        response.status shouldBe HttpStatus.BAD_REQUEST.statusCode
        val body = response.json().await()
        (body.error as Boolean) shouldBe true
        (body.message as String).contains("name cannot be blank") shouldBe true
    }
}
}
