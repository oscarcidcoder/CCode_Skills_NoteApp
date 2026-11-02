plugins {
    id("notetake.android.library")
    id("notetake.android.compose")
}

android {
    namespace = "org.cidcode.notetake.core.presentation"
}

dependencies {
    implementation(project(":core:domain"))
}
