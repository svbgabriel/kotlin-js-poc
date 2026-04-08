package io.github.svbgabriel.e2e

import io.github.svbgabriel.javascript.fetch
import io.github.svbgabriel.infrastructure.web.HttpStatus
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.await

class StatusE2ETest : E2ETestBase() {
    init {
        test("should return UP status from / endpoint") {
            val response = fetch("${baseUrl()}/").await()
            
            response.status shouldBe HttpStatus.OK.statusCode
            val body = response.json().await()
            (body.status as String) shouldBe "UP"
            
            // Check for checks map as well
            val checks = body.checks
            (checks.database.status as String) shouldBe "UP"
        }
    }
}
