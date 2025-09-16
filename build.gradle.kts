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

// Apply ktlint to all subprojects
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        version.set("1.0.1")
        debug.set(false)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)

        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

// Apply detekt to all subprojects
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        toolVersion = "1.23.4"
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        autoCorrect = true

        dependencies {
            detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.4")
            detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.4")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("codeQuality") {
    dependsOn("ktlintCheck", "detekt")
    group = "verification"
    description = "Run all code quality checks"
}

tasks.register("codeQualityFix") {
    dependsOn("ktlintFormat", "detektAutoCorrect")
    group = "verification"
    description = "Auto-fix code quality issues"
}