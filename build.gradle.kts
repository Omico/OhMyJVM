import me.omico.consensus.dsl.consensus

plugins {
    kotlin("multiplatform") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("com.diffplug.spotless") version "6.25.0"
    id("me.omico.consensus.spotless") version "0.8.0"
}

group = "me.omico.ojvm"
version = "2.2.0-SNAPSHOT"

kotlin {
    mingwX64 {
        binaries {
            executable {
                entryPoint = "me.omico.ojvm.main"
                linkerOpts("-lversion")
            }
        }
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cli.ExperimentalCli")
        }
        commonMain {
            dependencies {
                implementation("com.squareup.okio:okio:3.7.0")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
            }
        }
    }
}

consensus {
    spotless {
        freshmark()
        intelliJIDEARunConfiguration()
        kotlin(
            licenseHeaderFile = rootProject.file("spotless/copyright.kt"),
        )
        kotlinGradle()
    }
}
