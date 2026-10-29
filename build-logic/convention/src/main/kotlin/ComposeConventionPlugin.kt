package com.example.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation

fun Project.namespaceFromPath(default: String = "com.example.notesapp"): String {
    val segments = path.split(':').filter { it.isNotEmpty() }
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

                kotlinOptions {
                    jvmTarget = "17"
                    freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.12"
                }

                packagingOptions {
                    resources.excludes += "META-INF/*.kotlin_module"
                }
            }

            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.compose.bom)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.ui.graphics)
                implementation(libs.compose.ui.tooling)
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.compose.animation)
                implementation(libs.compose.animation.graphics)
                implementation(libs.compose.runtime)
                implementation(libs.compose.runtime.saveable)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}