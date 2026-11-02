plugins {
    id("notetake.android.library")
    id("notetake.android.compose")
    id("notetake.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "org.cidcode.notetake.feature.notes.presentation"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:presentation"))
    implementation(project(":core:design-system"))
    implementation(project(":feature:notes:domain"))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
}
