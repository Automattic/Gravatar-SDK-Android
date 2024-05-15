import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

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

    // Roborazzi
    id("io.github.takahirom.roborazzi") version "1.15.0" apply false
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.9.20")
        classpath("org.jetbrains.dokka:versioning-plugin:1.9.20")

        /**
         * Forcing the use of Jackson Core to avoid a conflict between the version used by
         * Dokka and the one used by OpenAPI Generator.
         */
        classpath("com.fasterxml.jackson.core:jackson-core:2.15.3")
    }
}

dependencies {
    dokkaHtmlMultiModulePlugin("org.jetbrains.dokka:versioning-plugin:1.9.20")
    dokkaHtmlMultiModulePlugin("org.jetbrains.dokka:android-documentation-plugin:1.9.20")
}

// Apply dokka and dokka plugins to all subprojects except the demo-app
subprojects {
    if (this.name != "demo-app") {
        apply {
            plugin("org.jetbrains.dokka")
        }
    }
}

val currentVersion = "0.3.0"

tasks.dokkaHtmlMultiModule {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/2231")
    val historyDir = projectDir.resolve("docs/dokka/history/")
    val currentDir = historyDir.resolve(currentVersion)

    moduleName.set("Gravatar Android SDK")
    outputDirectory.set(currentDir)

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(file("./docs/images/dokka/logo-icon.svg"))
        footerMessage = "© Automattic Inc."
    }

    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        version = currentVersion
        olderVersionsDir = historyDir
    }
}
