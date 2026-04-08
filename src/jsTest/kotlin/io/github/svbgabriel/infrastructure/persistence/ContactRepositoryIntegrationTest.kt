package io.github.svbgabriel.infrastructure.persistence

import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.infrastructure.config.Environment
import io.github.svbgabriel.infrastructure.database.MongoConfiguration
import io.github.svbgabriel.infrastructure.database.mongooseVal
import io.github.svbgabriel.testcontainers.GenericContainer
import io.github.svbgabriel.testcontainers.StartedTestContainer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.await
import kotlin.js.json

class ContactRepositoryIntegrationTest : FunSpec({

    lateinit var container: StartedTestContainer

    beforeSpec {
        container = GenericContainer("mongo:8.2")
            .withExposedPorts(27017)
            .withEnvironment(
                json(
                    "MONGO_INITDB_ROOT_USERNAME" to "admin",
                    "MONGO_INITDB_ROOT_PASSWORD" to "admin"
                )
            )
            .start()
            .await()

        Environment.set("DB_HOST", container.getHost())
        Environment.set("DB_PORT", container.getMappedPort(27017).toString())
        Environment.set("DB_USER", "admin")
        Environment.set("DB_PASS", "admin")
        Environment.set("DB_NAME", "test-contacts")
        Environment.set("DB_AUTH_SOURCE", "admin")

        mongooseVal.connect(MongoConfiguration.dbUrl).await()
    }

    afterSpec {
        mongooseVal.connection.close().await()
        container.stop().await()
    }

    test("should save and find a contact") {
        val repository = ContactRepositoryImpl()
        val contact = Contact(
            name = "Integration Test", nickname = "IT", email = "it@example.com",
            id = undefined,
            createdAt = undefined,
            updatedAt = undefined
        )

        val created = repository.create(contact)
        created.id.shouldNotBeNull()

        val found = repository.findById(created.id)
        found?.name shouldBe "Integration Test"
        found?.nickname shouldBe "IT"
        found?.email shouldBe "it@example.com"
    }

    test("should list all contacts") {
        val repository = ContactRepositoryImpl()
        val contacts = repository.findAll()
        // It might be more than 1 if tests run in the same container without clearing,
        // but here it's a fresh container per spec (FunSpec default)
        contacts.size shouldBe 1
    }
})
