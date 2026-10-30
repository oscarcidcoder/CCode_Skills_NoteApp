plugins {
    `kotlin-dsl`
}

// Declarar las convention plugins via gradlePlugin{} es lo que permite que el
// build incluido (includeBuild("build-logic")) las resuelva por id.
// Los descriptores META-INF/gradle-plugins/*.properties no bastan para eso.
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.example.buildlogic.android-application"
            implementationClass = "com.example.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "com.example.buildlogic.android-library"
            implementationClass = "com.example.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("compose") {
            id = "com.example.buildlogic.compose"
            implementationClass = "com.example.buildlogic.ComposeConventionPlugin"
        }
        register("domainModule") {
            id = "com.example.buildlogic.domain-module"
            implementationClass = "com.example.buildlogic.DomainModuleConventionPlugin"
        }
        register("koin") {
            id = "com.example.buildlogic.koin"
            implementationClass = "com.example.buildlogic.KoinConventionPlugin"
        }
        register("room") {
            id = "com.example.buildlogic.room"
            implementationClass = "com.example.buildlogic.RoomConventionPlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    // Las convention plugins referencian tipos y DSL de estos plugins en tiempo de
    // compilación (ApplicationExtension, LibraryExtension, kotlinOptions, ...).
    // compileOnly: los plugins reales los aporta el build que los aplica, no este modulo.
    compileOnly(libs.gradle.agp)
    compileOnly(libs.gradle.kotlin)
    compileOnly(libs.gradle.ksp)
}
