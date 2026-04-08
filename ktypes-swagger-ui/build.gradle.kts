plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinx.jsPlainObjects)
}

kotlin {
    js {
        nodejs {}
        useEsModules()
        compilerOptions {
            optIn.add("kotlin.js.ExperimentalJsExport")
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":ktypes-javascript"))
                implementation(project(":ktypes-express"))
                implementation(npm("swagger-ui-express", "^5.0.1"))
            }
        }
    }
}
