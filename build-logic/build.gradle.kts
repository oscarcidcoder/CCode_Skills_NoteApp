import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "org.cidcode.notetake.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "notetake.android.library"
            implementationClass = "org.cidcode.notetake.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "notetake.android.compose"
            implementationClass = "org.cidcode.notetake.buildlogic.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "notetake.android.hilt"
            implementationClass = "org.cidcode.notetake.buildlogic.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "notetake.android.room"
            implementationClass = "org.cidcode.notetake.buildlogic.AndroidRoomConventionPlugin"
        }
        register("kotlinModule") {
            id = "notetake.kotlin.module"
            implementationClass = "org.cidcode.notetake.buildlogic.KotlinModuleConventionPlugin"
        }
    }
}
