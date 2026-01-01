plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("ojvm.spotless-kotlin")
}

kotlin {
    mingwX64 {
        binaries {
            executable(namePrefix = "ojvm") {
                linkerOpts("-lversion")
            }
        }
    }
    macosArm64 {
        binaries {
            executable(namePrefix = "ojvm") {
                entryPoint = "me.omico.ojvm.main"
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
    commonMainImplementation(kotlinx.io.core)
    commonMainImplementation(kotlinx.serialization.json)
}
