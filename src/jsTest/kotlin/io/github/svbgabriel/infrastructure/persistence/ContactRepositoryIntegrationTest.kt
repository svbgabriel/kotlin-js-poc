package io.github.svbgabriel.infrastructure.persistence

import io.github.svbgabriel.domain.model.Contact
import io.github.svbgabriel.infrastructure.database.mongooseVal
import io.github.svbgabriel.infrastructure.externals.node.process
import io.github.svbgabriel.infrastructure.externals.testcontainers.GenericContainer
import io.github.svbgabriel.infrastructure.externals.testcontainers.StartedTestContainer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
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

        process.env.DB_HOST = container.getHost()
        process.env.DB_PORT = container.getMappedPort(27017).toString()
        process.env.DB_USER = "admin"
        process.env.DB_PASS = "admin"
        process.env.DB_NAME = "test-contacts"
        process.env.DB_AUTH_SOURCE = "admin"

        mongooseVal.connect(io.github.svbgabriel.config.Configuration.dbUrl).await()
    }

    afterSpec {
        mongooseVal.connection.close().await()
        container.stop().await()
    }

    test("should save and find a contact") {
        val repository = ContactRepositoryImpl()
        val contact = Contact(name = "Integration Test", nickname = "IT", email = "it@example.com")

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
        // It might be more than 1 if tests run in same container without clearing,
        // but here it's fresh container per spec (FunSpec default)
        contacts.size shouldBe 1
    }
})
