package com.example.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class RoomConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.google.devtools.ksp")

            configure<com.android.build.api.dsl.LibraryExtension> {
                namespace = namespaceFromPath()
                compileSdk = 34

                defaultConfig {
                    minSdk = 24
                    targetSdk = 34
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
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

                packagingOptions {
                    resources.excludes += "META-INF/*.kotlin_module"
                }
            }

            // En un plugin class-based no existen los accessors del DSL: kotlinOptions
            // no esta disponible dentro del bloque android, se configura en las tareas.
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                kotlinOptions.jvmTarget = "17"
                kotlinOptions.freeCompilerArgs += listOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xroom.enable-suspending-dao-queries"
                )
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("room-runtime").get())
                add("implementation", libs.findLibrary("room-ktx").get())
                add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())
                add("ksp", libs.findLibrary("room-compiler").get())
            }
        }
    }
}