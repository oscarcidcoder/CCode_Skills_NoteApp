package com.example.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation

class DomainModuleConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            group = "com.example.notesapp"
            version = "1.0"

            java {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(17))
                }
            }

            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
                kotlinOptions {
                    jvmTarget = "17"
                    freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
                }
            }

            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.assertk)
                testImplementation(libs.junit.jupiter.api)
                testImplementation(libs.turbine)
                testImplementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}