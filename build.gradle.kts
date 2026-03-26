import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

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

// Improve security by disabling automatic scripts execution (--ignore-scripts)
rootProject.plugins.withType<YarnPlugin> {
    rootProject.the<YarnRootExtension>().ignoreScripts = true
}

kotlin {
    js {
        nodejs {}
        useEsModules()
        binaries.executable()

        compilerOptions {
            // Generating source maps to enable debug
            sourceMap = true
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
        jsTest.dependencies {
            implementation(npm("testcontainers", "10.18.0"))
        }
        jsMain.dependencies {
            implementation(npm("express", "^5.2.1"))
            implementation(npm("mongoose", "^9.2.1"))
            implementation(npm("swagger-ui-express", "^5.0.1"))
        }
    }
}

tasks.withType<KotlinJsCompile>().configureEach {
    compilerOptions {
        target = "es2015"
    }
}

tasks.withType<KotlinJsTest>().configureEach {
    reports {
        html.required.set(true)
        junitXml.required.set(true)
    }
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}
