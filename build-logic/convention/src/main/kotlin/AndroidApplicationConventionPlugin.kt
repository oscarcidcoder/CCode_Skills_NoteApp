package com.example.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            configure<com.android.build.api.dsl.ApplicationExtension> {
                namespace = "com.example.notesapp"
                compileSdk = 34

                defaultConfig {
                    applicationId = "com.example.notesapp"
                    minSdk = 24
                    targetSdk = 34
                    versionCode = 1
                    versionName = "1.0"
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables.useSupportLibrary = true
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
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
                implementation(libs.activity.compose)
                implementation(libs.lifecycle.runtime.compose)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.lifecycle.viewmodel.ktx)
                implementation(libs.compose.bom)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.compose.animation)
                implementation(libs.compose.animation.graphics)
                implementation(libs.navigation.compose)
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
            }
        }
    }
}