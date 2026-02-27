package io.github.svbgabriel

import io.github.svbgabriel.infrastructure.externals.express.Request
import io.github.svbgabriel.infrastructure.externals.express.Response
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
