plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("org.jetbrains.kotlin.jvm") apply false
    id("org.jetbrains.kotlin.plugin.serialization") apply false
    id("com.google.devtools.ksp") apply false
}

allprojects {
    group = "com.example.notesapp"
    version = "1.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}