package io.github.svbgabriel.infrastructure.database

import io.github.svbgabriel.infrastructure.externals.mongoose.Mongoose
import io.github.svbgabriel.infrastructure.externals.mongoose.MongooseStatic

val mongooseVal: MongooseStatic
    get() = if (Mongoose.connect == undefined) Mongoose.default!! else Mongoose
