import me.omico.age.spotless.configureSpotless
import me.omico.age.spotless.intelliJIDEARunConfiguration
import me.omico.age.spotless.kotlin
import me.omico.age.spotless.kotlinGradle

plugins {
    kotlin("multiplatform") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("com.diffplug.spotless") version "6.16.0"
    id("me.omico.age.spotless") version "1.0.0-SNAPSHOT"
}

group = "me.omico.ojvm"
version = "1.0.0-SNAPSHOT"

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

configureSpotless {
    freshmark {
        target("**/*.md")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
    intelliJIDEARunConfiguration()
    kotlin(
        licenseHeaderFile = rootProject.file("spotless/copyright.kt"),
        licenseHeaderConfig = {
            updateYearWithLatest(true)
            yearSeparator("-")
        },
    )
    kotlinGradle()
}
