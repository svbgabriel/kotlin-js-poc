package io.github.svbgabriel.infrastructure.externals.mongoose

import kotlinx.js.JsPlainObject
import kotlin.js.Promise
import kotlin.js.Json

@JsModule("mongoose")
@JsNonModule
external val Mongoose: MongooseStatic

external interface MongooseStatic {
    val default: MongooseStatic?
    val connect: Any?
    fun connect(uri: String, options: Any = definedExternally): Promise<MongooseInstance>
    fun model(name: String, schema: Any): Model<Any>
    val Schema: Any
    val connection: Connection
}

external interface MongooseInstance {
    fun connect(uri: String, options: Any = definedExternally): Promise<MongooseInstance>
    val connection: Connection
    val Schema: Any
    fun <T> model(name: String, schema: Any): Model<T>
}

external interface Connection {
    fun on(event: String, callback: (Any?) -> Unit)
    val readyState: Int
    fun close(): Promise<Unit>
}

external interface Query<T> {
    fun lean(): Query<T>
    fun exec(): Promise<T>
    fun then(onFulfilled: ((T) -> Unit), onRejected: ((Throwable) -> Unit) = definedExternally): Promise<T>
}

external interface Model<T> {
    fun find(query: Json = definedExternally): Query<Array<T>>
    fun findOne(query: Json): Query<T?>
    fun create(doc: Any): Promise<T>
    fun deleteOne(query: Json): Promise<DeleteResult>
    fun updateOne(query: Json, update: Any, options: Any = definedExternally): Promise<UpdateResult>
    fun findById(id: String): Query<T?>
}

external interface DeleteResult {
    val deletedCount: Int
    val acknowledged: Boolean
}

external interface UpdateResult {
    val modifiedCount: Int
    val matchedCount: Int
    val upsertedCount: Int
    val upsertedId: Any?
    val acknowledged: Boolean
}

@JsPlainObject
external interface SchemaTypeOptions {
    val type: String
    val required: Boolean?
    val unique: Boolean?
}

@JsPlainObject
external interface SchemaOptions {
    val timestamps: Boolean?
}
