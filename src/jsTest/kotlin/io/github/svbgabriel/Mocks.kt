package io.github.svbgabriel

import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.infrastructure.externals.express.Request
import io.github.svbgabriel.infrastructure.externals.express.Response
import io.github.svbgabriel.domain.service.ContactService
import kotlin.js.json

class MockRequest(
    override val params: dynamic = json(),
    override val body: dynamic = json(),
    override val query: dynamic = json(),
    override val headers: dynamic = json()
) : Request

class MockResponse : Response {
    var statusCode: Int = 0
    var jsonBody: dynamic = null
    var sentBody: dynamic = null

    override fun status(code: Int): Response {
        statusCode = code
        return this
    }

    override fun json(body: dynamic): Response {
        jsonBody = body
        return this
    }

    override fun send(body: dynamic): Response {
        sentBody = body
        return this
    }

    override fun set(field: String, vararg value: String): Response {
        // Mock set
        return this
    }
}

class MockContactService : ContactService {
    var findAllResult: Array<Contact> = emptyArray()
    var findByIdResult: Contact? = null
    var createResult: Contact? = null
    var deleteResult: Boolean = false
    var modifiedResult: Boolean = false

    var shouldFail = false

    override suspend fun findAll(): Array<Contact> {
        if (shouldFail) throw Exception("Service error")
        return findAllResult
    }

    override suspend fun findById(id: String): Contact? {
        if (shouldFail) throw Exception("Service error")
        return findByIdResult
    }

    override suspend fun create(contact: Contact): Contact {
        if (shouldFail) throw Exception("Service error")
        return createResult ?: contact
    }

    override suspend fun update(id: String, contact: Contact): Boolean {
        if (shouldFail) throw Exception("Service error")
        return modifiedResult
    }

    override suspend fun delete(id: String): Boolean {
        if (shouldFail) throw Exception("Service error")
        return deleteResult
    }
}
