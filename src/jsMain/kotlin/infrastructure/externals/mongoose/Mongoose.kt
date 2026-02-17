package io.github.svbgabriel.infrastructure.externals.mongoose

import kotlin.js.Promise

@JsModule("mongoose")
@JsNonModule
external val Mongoose: dynamic

external interface MongooseInstance {
    fun connect(uri: String, options: dynamic = definedExternally): Promise<MongooseInstance>
    val connection: Connection
    val Schema: dynamic
    fun model(name: String, schema: dynamic): Model<dynamic>
}

external interface Connection {
    fun on(event: String, callback: (dynamic) -> Unit)
    val readyState: Int
    fun close(): Promise<Unit>
}

external interface Query<T> {
    fun lean(): Query<T>
    fun exec(): Promise<T>
    fun then(onFulfilled: ((T) -> Unit), onRejected: ((Throwable) -> Unit) = definedExternally): Promise<T>
}

external interface Model<T> {
    fun find(query: dynamic = definedExternally): Query<Array<T>>
    fun findOne(query: dynamic): Query<T?>
    fun create(doc: dynamic): Promise<T>
    fun deleteOne(query: dynamic): Promise<DeleteResult>
    fun updateOne(query: dynamic, update: dynamic, options: dynamic = definedExternally): Promise<UpdateResult>
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
    val upsertedId: dynamic
    val acknowledged: Boolean
}
