package io.github.svbgabriel.infrastructure.externals.express

import io.github.svbgabriel.infrastructure.web.HttpStatus
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import kotlinx.serialization.json.encodeToDynamic

object ExpressExtensions {
    val jsonSerializer = Json { ignoreUnknownKeys = true }

    /**
     * Deserializes the JSON body from the Express request into an object of type [T].
     *
     * @return The deserialized object of type [T].
     */
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> Request.receive(): T = jsonSerializer.decodeFromDynamic(this.body)

    /**
     * Serializes the provided [body] of type [T] to JSON and sends it as the response with the given HTTP [status].
     *
     * @param status The HTTP status code to set for the response.
     * @param body The object to be serialized and sent in the response body.
     */
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> Response.sendJson(status: HttpStatus, body: T) {
        this.status(status.statusCode).json(jsonSerializer.encodeToDynamic(body))
    }

    /**
     * Sends a 204 No Content response, indicating the request was successful but there is no body to return.
     */
    fun Response.sendNoContent() {
        this.status(HttpStatus.NO_CONTENT.statusCode).send(body = null)
    }
}
