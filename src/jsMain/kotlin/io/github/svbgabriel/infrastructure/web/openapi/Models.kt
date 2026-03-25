package io.github.svbgabriel.infrastructure.web.openapi

import kotlinx.js.JsPlainObject
import kotlin.js.Json

@JsPlainObject
external interface OpenApiSpec {
    @JsName("openapi")
    val openapi: String
    @JsName("info")
    val info: OpenApiInfo
    @JsName("paths")
    val paths: Json // Map<String, PathItem>
    @JsName("components")
    val components: OpenApiComponents?
}

@JsPlainObject
external interface OpenApiInfo {
    @JsName("title")
    val title: String
    @JsName("version")
    val version: String
    @JsName("description")
    val description: String?
}

@JsPlainObject
external interface PathItem {
    @JsName("get")
    val get: Operation?
    @JsName("post")
    val post: Operation?
    @JsName("put")
    val put: Operation?
    @JsName("delete")
    val delete: Operation?
    @JsName("patch")
    val patch: Operation?
}

@JsPlainObject
external interface Operation {
    @JsName("summary")
    val summary: String?
    @JsName("description")
    val description: String?
    @JsName("tags")
    val tags: Array<String>?
    @JsName("parameters")
    val parameters: Array<Parameter>?
    @JsName("requestBody")
    val requestBody: RequestBody?
    @JsName("responses")
    val responses: Json? // Map<String, ResponseItem>
    @JsName("operationId")
    val operationId: String?
}

@JsPlainObject
external interface Parameter {
    @JsName("name")
    val name: String
    @JsName("in")
    val `in`: String // "path", "query", "header", "cookie"
    @JsName("description")
    val description: String?
    @JsName("required")
    val required: Boolean?
    @JsName("schema")
    val schema: Schema?
}

@JsPlainObject
external interface RequestBody {
    @JsName("content")
    val content: Json? // Map<String, MediaType>
    @JsName("description")
    val description: String?
    @JsName("required")
    val required: Boolean?
}

@JsPlainObject
external interface ResponseItem {
    @JsName("description")
    val description: String
    @JsName("content")
    val content: Json? // Map<String, MediaType>
}

@JsPlainObject
external interface MediaType {
    @JsName("schema")
    val schema: Schema?
    @JsName("example")
    val example: Any?
    @JsName("examples")
    val examples: Json? // Map<String, Example>
}

@JsPlainObject
external interface Example {
    @JsName("summary")
    val summary: String?
    @JsName("description")
    val description: String?
    @JsName("value")
    val value: Any?
    @JsName("externalValue")
    val externalValue: String?
}

@JsPlainObject
external interface Schema {
    @JsName("type")
    val type: String?
    @JsName("format")
    val format: String?
    @JsName("properties")
    val properties: Json? // Map<String, Schema>
    @JsName("items")
    val items: Schema?
    @JsName("required")
    val required: Array<String>?
    @JsName("example")
    val example: Any?
    @JsName("examples")
    val examples: Array<Any>?
    @JsName("description")
    val description: String?
}

@JsPlainObject
external interface OpenApiComponents {
    @JsName("schemas")
    val schemas: Json? // Map<String, Schema>
}
