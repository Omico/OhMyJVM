import me.omico.consensus.dsl.consensus

plugins {
    kotlin("multiplatform") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.diffplug.spotless") version "6.25.0"
    id("me.omico.consensus.spotless") version "0.8.0"
}

group = "me.omico.ojvm"
version = "2.2.0-SNAPSHOT"

kotlin {
    mingwX64("native") {
        binaries {
            executable {
                entryPoint = "main"
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
                implementation("com.squareup.okio:okio:3.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
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
