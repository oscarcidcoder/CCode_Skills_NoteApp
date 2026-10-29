plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    // No direct dependencies needed; convention plugins reference
    // the version catalog via the project's settings.gradle.kts
}