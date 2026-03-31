package io.github.svbgabriel.e2e

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.svbgabriel.di.appModule
import io.github.svbgabriel.di.installDependencyInjectionModule
import io.github.svbgabriel.infrastructure.database.installDatabase
import io.github.svbgabriel.infrastructure.database.mongooseVal
import io.github.svbgabriel.infrastructure.web.WebServer
import io.github.svbgabriel.infrastructure.externals.node.process
import io.github.svbgabriel.infrastructure.externals.testcontainers.GenericContainer
import io.github.svbgabriel.infrastructure.externals.testcontainers.StartedTestContainer
import io.github.svbgabriel.infrastructure.web.embeddedServer
import io.github.svbgabriel.infrastructure.web.openapi.OpenApiInfo
import io.github.svbgabriel.infrastructure.web.openapi.installOpenApi
import io.github.svbgabriel.infrastructure.web.plugin.installAppInfrastructure
import io.github.svbgabriel.infrastructure.web.routes.installContactRoutes
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.await
import org.koin.core.context.stopKoin
import kotlin.js.Promise
import kotlin.js.json

open class E2ETestBase : FunSpec() {
    private val logger = KotlinLogging.logger("E2ETestBase")
    private lateinit var container: StartedTestContainer
    private var server: WebServer? = null
    private var serverPort: Int = 0

    fun baseUrl() = "http://localhost:$serverPort"

    init {
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
            process.env.DB_NAME = "e2e-contacts"
            process.env.DB_AUTH_SOURCE = "admin"

            server = Promise<WebServer> { resolve, _ ->
                embeddedServer(
                    overridePort = 0,
                    onListen = { s ->
                        val address = js("s.address()")
                        serverPort = address.port as Int
                        logger.info { "E2E Server started on port $serverPort" }
                        resolve(s)
                    }
                ) {
                    installDependencyInjectionModule {
                        modules(appModule)
                    }
                    installDatabase()

                    installAppInfrastructure()
                    installOpenApi(
                        info = OpenApiInfo(
                            title = "E2E Contacts API",
                            version = "1.0.0",
                            description = "E2E API for testing"
                        )
                    )
                    installContactRoutes()
                }
            }.await()
        }

        afterSpec {
            Promise { resolve, _ ->
                server?.close { resolve(Unit) } ?: resolve(Unit)
            }.await()
            mongooseVal.connection.close().await()
            stopKoin()
            container.stop().await()
        }
    }
}
