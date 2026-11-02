package org.cidcode.notetake.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

internal val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

internal fun Project.addTestDependencies() {
    dependencies.add("testImplementation", libs.findLibrary("junit-jupiter").get())
    dependencies.add("testImplementation", libs.findLibrary("assertk").get())
    dependencies.add("testImplementation", libs.findLibrary("turbine").get())
    dependencies.add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
    dependencies.add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
}
