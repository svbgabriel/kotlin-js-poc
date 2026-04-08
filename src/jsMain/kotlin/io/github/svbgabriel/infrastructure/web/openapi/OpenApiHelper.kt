package io.github.svbgabriel.infrastructure.web.openapi

import io.github.svbgabriel.swagger.MediaType
import io.github.svbgabriel.swagger.RequestBody
import io.github.svbgabriel.swagger.Schema
import kotlin.js.Json
import kotlin.js.json

/**
 * Helper to build OpenAPI metadata in a cleaner way.
 */
object OpenApiHelper {
    fun response(description: String, contentType: String = "application/json", schema: Schema? = null): Json {
        val content = schema?.let {
            json(contentType to MediaType(schema = it))
        } ?: undefined
        return json("description" to description, "content" to content)
    }

    fun requestBody(description: String, contentType: String = "application/json", schema: Schema? = null, required: Boolean = true): RequestBody {
        val content = schema?.let {
            json(contentType to MediaType(schema = it))
        } ?: undefined
        return RequestBody(description = description, content = content, required = required)
    }

    fun schema(
        type: String? = null,
        format: String? = null,
        properties: Map<String, Schema>? = null,
        items: Schema? = null,
        required: List<String>? = null,
        example: Any? = null
    ): Schema {
        val propsJson = properties?.let {
            val j = json()
            it.forEach { (k, v) -> j[k] = v }
            j
        }
        return Schema(
            type = type ?: undefined,
            format = format ?: undefined,
            properties = propsJson ?: undefined,
            items = items ?: undefined,
            required = required?.toTypedArray() ?: undefined,
            example = example ?: undefined
        )
    }
}
