plugins {
    id("me.omico.consensus.spotless")
}

consensus {
    spotless {
        kotlin(
            licenseHeaderFile = rootProject.file("spotless/copyright.kt").takeIf(File::exists),
        )
        kotlinGradle()
    }
}
