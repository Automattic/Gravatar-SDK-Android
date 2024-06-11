import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false

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

// Apply dokka to all subprojects except the demo-app
subprojects {
    if (this.name != "demo-app") {
        apply {
            plugin("org.jetbrains.dokka")
        }
    }
}

// Semantic versioning for release version
val versionName = "1.0.0"

tasks.dokkaHtmlMultiModule {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/2231")
    val mainDir = projectDir.resolve("docs/dokka/") // docs/dokka/
    val historyDir = mainDir.resolve("history/") // docs/dokka/history/ - all versions
    val currentDir = mainDir.resolve("current/") // docs/dokka/current/ - what we'll serve in github pages
    val newVersionDir = historyDir.resolve(versionName) // docs/dokka/history/x.x.x/ - new version

    moduleName.set("Gravatar Android SDK")
    outputDirectory.set(newVersionDir)

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(file("./docs/images/dokka/logo-icon.svg"))
        footerMessage = "© Automattic Inc."
    }

    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        version = versionName
        olderVersionsDir = historyDir
    }

    doLast {
        // Delete the current directory, it will be replaced by the new version
        currentDir.deleteRecursively()
        // Copy the new version to the current directory
        newVersionDir.copyRecursively(currentDir)
        // Delete the "older" directory from the new version
        newVersionDir.resolve("older").deleteRecursively()
    }
}
