package com.example.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.namespaceFromPath(default: String = "com.example.notesapp"): String {
    val segments = path.split(':')
        .filter { it.isNotEmpty() }
        // Los nombres de modulo pueden tener guiones (design-system); un namespace
        // de Android debe ser un paquete Java valido.
        .map { it.replace(Regex("[^A-Za-z0-9]+"), "") }
    if (segments.isEmpty()) return default
    return "$default.${segments.joinToString(".")}"
}

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            configure<com.android.build.api.dsl.LibraryExtension> {
                namespace = namespaceFromPath()
                compileSdk = 34

                buildFeatures {
                    compose = true
                }

                defaultConfig {
                    minSdk = 24
                    targetSdk = 34
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                    vectorDrawables.useSupportLibrary = true
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.12"
                }

                packagingOptions {
                    resources.excludes += "META-INF/*.kotlin_module"
                }
            }

            // En un plugin class-based no existen los accessors del DSL: kotlinOptions
            // no esta disponible dentro del bloque android, se configura en las tareas.
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                kotlinOptions.jvmTarget = "17"
                kotlinOptions.freeCompilerArgs += listOf(
                    "-Xopt-in=kotlin.RequiresOptIn"
                )
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", platform(libs.findLibrary("compose-bom").get()))
                add("implementation", libs.findLibrary("compose-material3").get())
                add("implementation", libs.findLibrary("compose-ui").get())
                add("implementation", libs.findLibrary("compose-ui-graphics").get())
                add("implementation", libs.findLibrary("compose-ui-tooling").get())
                add("implementation", libs.findLibrary("compose-ui-tooling-preview").get())
                add("implementation", libs.findLibrary("compose-animation").get())
                add("implementation", libs.findLibrary("compose-animation-graphics").get())
                add("implementation", libs.findLibrary("compose-runtime").get())
                add("implementation", libs.findLibrary("compose-runtime-saveable").get())
                add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())
                add("implementation", libs.findLibrary("kotlinx-serialization-json").get())
                add("implementation", libs.findLibrary("kotlinx-datetime").get())
            }
        }
    }
}