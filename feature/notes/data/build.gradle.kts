plugins {
    id("com.example.buildlogic.compose")
    id("com.example.buildlogic.koin")
    id("com.example.buildlogic.room")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":feature:notes:domain"))
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
}