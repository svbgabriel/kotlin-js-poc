plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinx.jsPlainObjects)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
    alias(libs.plugins.mokkery)
}

group = "io.github.svbgabriel"
version = "1.0.0"

kotlin {
    js {
        nodejs {}
        useEsModules()
        binaries.executable()

        compilerOptions {
            optIn.add("kotlin.js.ExperimentalJsExport")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.koin.core)
            implementation(libs.konform)
        }
        commonTest.dependencies {
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotlinx.coroutines.test)
        }
        jsMain.dependencies {
            implementation(npm("express", "^5.2.1"))
            implementation(npm("mongoose", "^9.2.1"))
        }
    }
}
