package com.example.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
            }
        }
    }
}