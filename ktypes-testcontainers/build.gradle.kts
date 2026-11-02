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
        jsMain.dependencies {
            implementation(npm("testcontainers", libs.versions.testcontainers.get()))
        }
    }
}
