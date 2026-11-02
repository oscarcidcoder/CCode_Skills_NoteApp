package org.cidcode.notetake.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }

            val composeBom = dependencies.platform(libs.findLibrary("androidx-compose-bom").get())
            dependencies.add("implementation", composeBom)
            dependencies.add("androidTestImplementation", composeBom)
            dependencies.add("implementation", libs.findLibrary("androidx-compose-ui").get())
            dependencies.add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
            dependencies.add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            dependencies.add("implementation", libs.findLibrary("androidx-compose-material3").get())
            dependencies.add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
            dependencies.add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
        }
    }
}
