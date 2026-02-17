package io.github.svbgabriel.infrastructure.database

import io.github.svbgabriel.infrastructure.externals.mongoose.Mongoose

val mongooseVal: dynamic
    get() = if (Mongoose.connect == undefined) Mongoose.default else Mongoose
