plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        nodejs {}
        useEsModules()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
            }
        }
    }
}
