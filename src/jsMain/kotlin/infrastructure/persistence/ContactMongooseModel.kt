package io.github.svbgabriel.infrastructure.persistence

import io.github.svbgabriel.infrastructure.database.mongooseVal
import io.github.svbgabriel.infrastructure.externals.mongoose.Model
import kotlin.js.json

private fun newSchema(ctor: dynamic, definition: dynamic, options: dynamic): dynamic =
    js("new ctor(definition, options)")

private fun createSchema(definition: dynamic, options: dynamic = json()): dynamic {
    return newSchema(mongooseVal.Schema, definition, options)
}

private fun <T> defineModel(name: String, schema: dynamic): Model<T> {
    return (mongooseVal.model(name, schema) as Any).unsafeCast<Model<T>>()
}

val contactSchema = createSchema(
    json(
        "name" to json(
            "type" to "String",
            "required" to true
        ),
        "nickname" to json(
            "type" to "String",
            "required" to true,
            "unique" to true
        ),
        "email" to json(
            "type" to "String",
            "required" to true,
            "unique" to true
        )
    ),
    json("timestamps" to true)
)

val ContactModel = defineModel<ContactEntity>("Contact", contactSchema)
