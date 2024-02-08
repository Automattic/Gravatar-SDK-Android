// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    // Ktlint
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0" apply false

    // Detekt
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false

    // Dokka
    id("org.jetbrains.dokka") version "1.9.10"
}
