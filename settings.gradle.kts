pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("kotlinWrappers") {
            val wrappersVersion = "2026.2.17"
            from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:$wrappersVersion")
        }
    }
}

rootProject.name = "contacts"
include(":ktypes-express")
include(":ktypes-mongoose")
include(":ktypes-swagger-ui")
include(":ktypes-dotenv")
include(":ktypes-node")
include(":ktypes-javascript")
include(":ktypes-testcontainers")
