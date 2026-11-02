plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        nodejs {}
        useEsModules()
    }

    sourceSets {
        jsMain.dependencies {
            // Node.js built-ins don't need npm dependency here for external declarations
        }
    }
}
