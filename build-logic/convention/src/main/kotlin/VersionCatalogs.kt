package com.example.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

/**
 * Acceso al version catalog ("libs") desde las convention plugins.
 *
 * Gradle no expone los type-safe accessors de `libs` dentro de un build incluido
 * (build-logic), así que se resuelve a mano vía VersionCatalogsExtension.
 */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * `platform(...)` para una libreria del catalogo, en codigo class-based.
 *
 * En un .gradle.kts existe `platform(...)` como funcion del DSL, pero no hay
 * equivalente importable desde una convention plugin: se usa el modificador
 * de dependencias que Gradle registra en el DependencyHandler.
 */
internal fun Project.platform(library: Provider<MinimalExternalModuleDependency>): Any {
    val modifier = dependencies.extensions
        .getByType(PlatformDependencyModifiers::class.java)
        .platform
    return modifier.modify(library)
}
