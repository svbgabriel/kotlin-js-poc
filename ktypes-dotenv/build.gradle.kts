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
                implementation(npm("dotenv", "^17.3.1"))
            }
        }
    }
}
