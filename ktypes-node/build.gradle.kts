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
                // Node.js built-ins don't need npm dependency here for external declarations
            }
        }
    }
}
