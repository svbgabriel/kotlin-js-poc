package io.github.svbgabriel.infrastructure.database

import io.github.svbgabriel.mongoose.Mongoose
import io.github.svbgabriel.mongoose.MongooseStatic

val mongooseVal: MongooseStatic
    get() = if (Mongoose.connect == undefined) Mongoose.default!! else Mongoose
