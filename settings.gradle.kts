@file:Suppress("UnstableApiUsage")

rootProject.name = "ojvm"

pluginManagement {
    repositories {
        maven(url = "https://maven.omico.me")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url = "https://maven.omico.me")
        mavenCentral()
    }
}
