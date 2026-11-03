package io.github.svbgabriel.infrastructure.persistence

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.github.svbgabriel.infrastructure.web.HttpStatus

class ErrorMapperTest : FunSpec({

    test("should return null for non-Mongoose errors") {
        val err = RuntimeException("Something broke")
        val result = mapMongooseError(err)
        result shouldBe null
    }

    test("should map MongoServerError 11000 to CONFLICT") {
        val err = createMongooseError("MongoServerError", 11000, "E11000 duplicate key error")
        val result = mapMongooseError(err)

        result shouldBe Triple(HttpStatus.CONFLICT, "E11000 duplicate key error", emptyList())
    }

    test("should map MongoServerError other code to INTERNAL_SERVER_ERROR") {
        val err = createMongooseError("MongoServerError", 500, "Network timeout")
        val result = mapMongooseError(err)

        result shouldBe Triple(HttpStatus.INTERNAL_SERVER_ERROR, "Network timeout", emptyList())
    }

    test("should map ValidationError to BAD_REQUEST") {
        val err = createMongooseError("ValidationError", null, "Validation failed")
        val result = mapMongooseError(err)

        result!!.first shouldBe HttpStatus.BAD_REQUEST
        result.second shouldBe "Validation failed"
    }

    test("should map unknown Mongoose error to INTERNAL_SERVER_ERROR") {
        val err = createMongooseError("CastError", null, "Cast to ObjectId failed")
        val result = mapMongooseError(err)

        result!!.first shouldBe HttpStatus.INTERNAL_SERVER_ERROR
        result.second shouldBe "Cast to ObjectId failed"
    }

    test("should use default message when Mongoose message is blank") {
        val err = createMongooseError("MongoServerError", 11000, "")
        val result = mapMongooseError(err)

        result!!.second shouldBe "Duplicate entry detected"
    }
})

/**
 * Creates a fake Mongoose-like error for testing purposes.
 */
private fun createMongooseError(name: String, code: Number?, message: String): Throwable {
    val error = Error(message)
    error.asDynamic().name = name
    if (code != null) {
        error.asDynamic().code = code
    }
    error.asDynamic().message = message
    return error
}
