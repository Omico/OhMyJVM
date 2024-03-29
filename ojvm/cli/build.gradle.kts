plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    mingwX64 {
        binaries {
            executable(namePrefix = "ojvm") {
                entryPoint = "me.omico.ojvm.main"
                linkerOpts("-lversion")
            }
        }
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cli.ExperimentalCli")
        }
    }
}

dependencies {
    commonMainImplementation(kotlinx.cli)
    commonMainImplementation(kotlinx.serialization.json)
    commonMainImplementation(okio)
}
