package io.github.svbgabriel.infrastructure.persistence

import io.github.svbgabriel.infrastructure.database.mongooseVal
import io.github.svbgabriel.infrastructure.externals.mongoose.Model
import io.github.svbgabriel.infrastructure.externals.mongoose.SchemaOptions
import io.github.svbgabriel.infrastructure.externals.mongoose.SchemaTypeOptions
import kotlinx.js.JsPlainObject

private fun newSchema(ctor: Any, definition: Any, options: Any?): Any =
    js("new ctor(definition, options)")

private fun createSchema(definition: ContactSchemaDefinition, options: SchemaOptions): Any {
    return newSchema(mongooseVal.Schema, definition, options)
}

private fun <T> defineModel(name: String, schema: Any): Model<T> {
    return (mongooseVal.model(name, schema) as Any).unsafeCast<Model<T>>()
}

@JsPlainObject
external interface ContactSchemaDefinition {
    val name: SchemaTypeOptions
    val nickname: SchemaTypeOptions
    val email: SchemaTypeOptions
}

val contactSchema = createSchema(
    ContactSchemaDefinition(
        name = SchemaTypeOptions(
            type = "String",
            required = true
        ),
        nickname = SchemaTypeOptions(
            type = "String",
            required = true,
            unique = true
        ),
        email = SchemaTypeOptions(
            type = "String",
            required = true,
            unique = true
        )
    ),
    SchemaOptions(timestamps = true)
)

val ContactModel = defineModel<ContactEntity>("Contact", contactSchema)
