pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.5"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        versions("1.20.5", "1.20.6", "1.21.3", "1.21.4")
        vcsVersion = "1.21.4"
    }
    create(rootProject)
}

rootProject.name = "Keep XP"
