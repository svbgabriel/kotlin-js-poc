package io.github.svbgabriel.e2e

import io.github.svbgabriel.javascript.fetch
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.await

class SwaggerE2ETest : E2ETestBase() {
    init {
        test("should access swagger-ui-express docs") {
            val response = fetch("${baseUrl()}/api-docs/").await()
            response.status shouldBe 200

            val text = response.text().await()
            text shouldContain "swagger-ui"
        }

        test("should return valid openapi spec with tags and schemas") {
            val response = fetch("${baseUrl()}/api-docs/json").await()
            response.status shouldBe 200

            val spec = response.json().await()
            (spec.openapi as String) shouldBe "3.0.0"
            (spec.info.title as String) shouldNotBe null

            val paths = spec.paths
            val contactsPath = paths["/api/contacts"]
            (contactsPath.get.summary as String) shouldBe "List all contacts"

            val tags = contactsPath.get.tags as Array<String>
            tags shouldContain "Contacts"
        }
    }
}
