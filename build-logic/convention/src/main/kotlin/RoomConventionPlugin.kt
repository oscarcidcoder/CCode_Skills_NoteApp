package com.example.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.ksp

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

                kotlinOptions {
                    jvmTarget = "17"
                    freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xroom.enable-suspending-dao-queries")
                }

                packagingOptions {
                    resources.excludes += "META-INF/*.kotlin_module"
                }
            }

            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.room.runtime)
                implementation(libs.room.ktx)
                implementation(libs.kotlinx.coroutines.android)
                ksp(libs.room.compiler)
            }
        }
    }
}