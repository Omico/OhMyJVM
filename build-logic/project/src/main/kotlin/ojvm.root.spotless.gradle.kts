import me.omico.consensus.api.dsl.requireRootProject
import me.omico.consensus.spotless.ConsensusSpotlessDefaults

plugins {
    id("me.omico.consensus.spotless")
}

requireRootProject()

consensus {
    spotless {
        freshmark()
        gradleProperties()
        kotlin(
            targets =
                ConsensusSpotlessDefaults.Kotlin.targets(
                    "build-logic/**/src/main/kotlin/**/*.kt",
                ),
            licenseHeaderFile = rootProject.file("spotless/copyright.kt").takeIf(File::exists),
        )
        kotlinGradle(
            targets =
                ConsensusSpotlessDefaults.KotlinGradle.targets(
                    "build-logic/*/src/**/*.gradle.kts",
                ),
        )
    }
}
