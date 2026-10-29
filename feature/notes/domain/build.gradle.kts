plugins {
    id("com.example.buildlogic.domain-module")
}

dependencies {
    implementation(project(":core:domain"))
}