plugins {
    id("notetake.android.library")
    id("notetake.android.hilt")
    id("notetake.android.room")
}

android {
    namespace = "org.cidcode.notetake.feature.notes.data"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":feature:notes:domain"))
    implementation(libs.kotlinx.coroutines.core)
}
