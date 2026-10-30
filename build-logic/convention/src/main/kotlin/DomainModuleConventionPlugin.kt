package com.example.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class DomainModuleConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            group = "com.example.notesapp"
            version = "1.0"

            extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
                toolchain {
                    languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(17))
                }
            }

            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                kotlinOptions.jvmTarget = "17"
                kotlinOptions.freeCompilerArgs += listOf(
                    "-Xopt-in=kotlin.RequiresOptIn"
                )
            }

            dependencies {
                add("implementation", libs.findLibrary("kotlin-stdlib").get())
                add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
                add("implementation", libs.findLibrary("kotlinx-serialization-json").get())
                add("implementation", libs.findLibrary("kotlinx-datetime").get())
                add("testImplementation", libs.findLibrary("junit-jupiter-api").get())
                add("testImplementation", libs.findLibrary("turbine").get())
                add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
                add("testImplementation", libs.findLibrary("assertk").get())
            }
        }
    }
}