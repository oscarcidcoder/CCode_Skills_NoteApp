plugins {
    id("com.example.buildlogic.compose")
    id("com.example.buildlogic.koin")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:presentation"))
}