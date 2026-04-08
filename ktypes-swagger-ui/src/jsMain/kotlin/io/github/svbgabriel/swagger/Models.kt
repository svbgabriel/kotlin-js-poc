package io.github.svbgabriel.swagger

import kotlinx.js.JsPlainObject
import kotlin.js.Json

/**
 * Represents an OpenAPI specification.
 */
@JsPlainObject
external interface OpenApiSpec {
    /**
     * The Version of the OpenAPI specification.
     */
    @JsName("openapi")
    val openapi: String

    /**
     * Information about the API.
     */
    @JsName("info")
    val info: OpenApiInfo

    /**
     * The paths of the API.
     */
    @JsName("paths")
    val paths: Json // Map<String, PathItem>

    /**
     * The components of the API.
     */
    @JsName("components")
    val components: OpenApiComponents?
}

/**
 * Information about an OpenAPI specification.
 */
@JsPlainObject
external interface OpenApiInfo {
    /**
     * The title of the API.
     */
    @JsName("title")
    val title: String

    /**
     * The version of the API.
     */
    @JsName("version")
    val version: String

    /**
     * The description of the API.
     */
    @JsName("description")
    val description: String?
}

/**
 * Represents a path item in an OpenAPI specification.
 */
@JsPlainObject
external interface PathItem {
    /**
     * The GET operation.
     */
    @JsName("get")
    val get: Operation?

    /**
     * The POST operation.
     */
    @JsName("post")
    val post: Operation?

    /**
     * The PUT operation.
     */
    @JsName("put")
    val put: Operation?

    /**
     * The DELETE operation.
     */
    @JsName("delete")
    val delete: Operation?

    /**
     * The PATCH operation.
     */
    @JsName("patch")
    val patch: Operation?
}

/**
 * Represents an operation in an OpenAPI specification.
 */
@JsPlainObject
external interface Operation {
    /**
     * The summary of the operation.
     */
    @JsName("summary")
    val summary: String?

    /**
     * The description of the operation.
     */
    @JsName("description")
    val description: String?

    /**
     * The tags of the operation.
     */
    @JsName("tags")
    val tags: Array<String>?

    /**
     * The parameters of the operation.
     */
    @JsName("parameters")
    val parameters: Array<Parameter>?

    /**
     * The request body of the operation.
     */
    @JsName("requestBody")
    val requestBody: RequestBody?

    /**
     * The responses of the operation.
     */
    @JsName("responses")
    val responses: Json? // Map<String, ResponseItem>

    /**
     * The operation ID.
     */
    @JsName("operationId")
    val operationId: String?
}

/**
 * Represents a parameter in an OpenAPI operation.
 */
@JsPlainObject
external interface Parameter {
    /**
     * The name of the parameter.
     */
    @JsName("name")
    val name: String

    /**
     * The location of the parameter (e.g., "path", "query", "header", "cookie").
     */
    @JsName("in")
    val `in`: String // "path", "query", "header", "cookie"

    /**
     * The description of the parameter.
     */
    @JsName("description")
    val description: String?

    /**
     * Whether the parameter is required.
     */
    @JsName("required")
    val required: Boolean?

    /**
     * The schema of the parameter.
     */
    @JsName("schema")
    val schema: Schema?
}

/**
 * Represents a request body in an OpenAPI operation.
 */
@JsPlainObject
external interface RequestBody {
    /**
     * The content of the request body.
     */
    @JsName("content")
    val content: Json? // Map<String, MediaType>

    /**
     * The description of the request body.
     */
    @JsName("description")
    val description: String?

    /**
     * Whether the request body is required.
     */
    @JsName("required")
    val required: Boolean?
}

/**
 * Represents a response item in an OpenAPI operation.
 */
@JsPlainObject
external interface ResponseItem {
    /**
     * The description of the response.
     */
    @JsName("description")
    val description: String

    /**
     * The content of the response.
     */
    @JsName("content")
    val content: Json? // Map<String, MediaType>
}

/**
 * Represents a media type in an OpenAPI specification.
 */
@JsPlainObject
external interface MediaType {
    /**
     * The schema of the media type.
     */
    @JsName("schema")
    val schema: Schema?

    /**
     * The example for the media type.
     */
    @JsName("example")
    val example: Any?

    /**
     * The examples for the media type.
     */
    @JsName("examples")
    val examples: Json? // Map<String, Example>
}

/**
 * Represents an example in an OpenAPI specification.
 */
@JsPlainObject
external interface Example {
    /**
     * The summary of the example.
     */
    @JsName("summary")
    val summary: String?

    /**
     * The description of the example.
     */
    @JsName("description")
    val description: String?

    /**
     * The value of the example.
     */
    @JsName("value")
    val value: Any?

    /**
     * The external value of the example.
     */
    @JsName("externalValue")
    val externalValue: String?
}

/**
 * Represents a schema in an OpenAPI specification.
 */
@JsPlainObject
external interface Schema {
    /**
     * The type of the schema.
     */
    @JsName("type")
    val type: String?

    /**
     * The format of the schema.
     */
    @JsName("format")
    val format: String?

    /**
     * The properties of the schema.
     */
    @JsName("properties")
    val properties: Json? // Map<String, Schema>

    /**
     * The items of the schema (for array type).
     */
    @JsName("items")
    val items: Schema?

    /**
     * The required properties of the schema.
     */
    @JsName("required")
    val required: Array<String>?

    /**
     * The example for the schema.
     */
    @JsName("example")
    val example: Any?

    /**
     * The examples for the schema.
     */
    @JsName("examples")
    val examples: Array<Any>?

    /**
     * The description of the schema.
     */
    @JsName("description")
    val description: String?
}

/**
 * Represents components in an OpenAPI specification.
 */
@JsPlainObject
external interface OpenApiComponents {
    /**
     * The schemas of the components.
     */
    @JsName("schemas")
    val schemas: Json? // Map<String, Schema>
}
