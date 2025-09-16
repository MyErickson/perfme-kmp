plugins {
    // Kotlin Multiplatform
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false

    // Code quality
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false

    // Documentation
    alias(libs.plugins.dokka) apply false
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}