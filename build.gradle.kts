import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    // Ktlint
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0" apply false

    // Detekt
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false

    // Dokka
    id("org.jetbrains.dokka")
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.9.20")
    }
}

tasks.dokkaHtmlMultiModule {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/2231")

    moduleName.set("Gravatar Android SDK")
    outputDirectory.set(file("docs/dokka"))

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(file("./docs/images/dokka/logo-icon.svg"))
        footerMessage = "© Automattic Inc."
    }
}