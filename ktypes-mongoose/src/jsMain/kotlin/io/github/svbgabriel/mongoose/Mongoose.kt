package io.github.svbgabriel.mongoose

import kotlinx.js.JsPlainObject
import kotlin.js.Promise
import kotlin.js.Json

/**
 * External module for Mongoose.
 * Mongoose is a MongoDB object modeling tool designed to work in an asynchronous environment.
 */
@JsModule("mongoose")
@JsNonModule
external val Mongoose: MongooseStatic

/**
 * Represents the static Mongoose object.
 */
external interface MongooseStatic {
    /**
     * The default Mongoose instance.
     */
    val default: MongooseStatic?

    /**
     * Connect to a MongoDB instance.
     */
    val connect: Any?

    /**
     * Connects to MongoDB.
     * @param uri The MongoDB connection string.
     * @param options The connection options.
     * @return A [Promise] that resolves to a [MongooseInstance].
     */
    fun connect(uri: String, options: Any = definedExternally): Promise<MongooseInstance>

    /**
     * Defines a model or retrieves it.
     * @param name The model name.
     * @param schema The schema object.
     * @return A [Model] instance.
     */
    fun model(name: String, schema: Any): Model<Any>

    /**
     * The Schema constructor.
     */
    val Schema: Any

    /**
     * The default connection.
     */
    val connection: Connection
}

/**
 * Represents a Mongoose instance.
 */
external interface MongooseInstance {
    /**
     * Connects to MongoDB.
     */
    fun connect(uri: String, options: Any = definedExternally): Promise<MongooseInstance>

    /**
     * The connection used by this instance.
     */
    val connection: Connection

    /**
     * The Schema constructor.
     */
    val Schema: Any

    /**
     * Defines a model or retrieves it.
     */
    fun <T> model(name: String, schema: Any): Model<T>
}

/**
 * Represents a Mongoose connection.
 */
external interface Connection {
    /**
     * Registers a listener for a connection event.
     */
    fun on(event: String, callback: (Any?) -> Unit)

    /**
     * The state of the connection (0 = disconnected, 1 = connected, 2 = connecting, 3 = disconnecting).
     */
    val readyState: Int

    /**
     * Closes the connection.
     */
    fun close(): Promise<Unit>
}

/**
 * Represents a Mongoose query.
 */
external interface Query<T> {
    /**
     * Sets the lean option.
     */
    fun lean(): Query<T>

    /**
     * Executes the query.
     */
    fun exec(): Promise<T>

    /**
     * Handles the result of the query.
     */
    fun then(onFulfilled: ((T) -> Unit), onRejected: ((Throwable) -> Unit) = definedExternally): Promise<T>
}

/**
 * Represents a Mongoose model.
 */
external interface Model<T> {
    /**
     * Finds documents.
     */
    fun find(query: Json = definedExternally): Query<Array<T>>

    /**
     * Finds a single document.
     */
    fun findOne(query: Json): Query<T?>

    /**
     * Creates a new document.
     */
    fun create(doc: Any): Promise<T>

    /**
     * Deletes one document.
     */
    fun deleteOne(query: Json): Promise<DeleteResult>

    /**
     * Updates one document.
     */
    fun updateOne(query: Json, update: Any, options: Any = definedExternally): Promise<UpdateResult>

    /**
     * Finds a document by its id.
     */
    fun findById(id: String): Query<T?>
}

/**
 * Result of a delete operation.
 */
external interface DeleteResult {
    /**
     * Number of documents deleted.
     */
    val deletedCount: Int

    /**
     * Indicates if the operation was acknowledged by MongoDB.
     */
    val acknowledged: Boolean
}

/**
 * Result of an update operation.
 */
external interface UpdateResult {
    /**
     * Number of documents modified.
     */
    val modifiedCount: Int

    /**
     * Number of documents matched by the query.
     */
    val matchedCount: Int

    /**
     * Number of documents upserted.
     */
    val upsertedCount: Int

    /**
     * The _id of the upserted document.
     */
    val upsertedId: Any?

    /**
     * Indicates if the operation was acknowledged by MongoDB.
     */
    val acknowledged: Boolean
}

/**
 * Options for a schema type.
 */
@JsPlainObject
external interface SchemaTypeOptions {
    /**
     * The type of the field.
     */
    val type: String

    /**
     * Whether the field is required.
     */
    val required: Boolean?

    /**
     * Whether the field must be unique.
     */
    val unique: Boolean?
}

/**
 * Options for a schema.
 */
@JsPlainObject
external interface SchemaOptions {
    /**
     * Whether to automatically add createdAt and updatedAt timestamps.
     */
    val timestamps: Boolean?
}
