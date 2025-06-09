import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

group = "com.gravatar.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.vanniktech.maven.publishPlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.openapi.generator.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.roborazzi.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("mavenCentralPublish") {
            id = libs.plugins.gravatar.maven.publish.get().pluginId
            implementationClass = "MavenPublishConventionPlugin"
        }
        register("gravatarAndroidLibrary") {
            id = libs.plugins.gravatar.android.library.get().pluginId
            implementationClass = "GravatarAndroidLibraryConventionPlugin"
        }
        register("gravatarAndroidCompose") {
            id = libs.plugins.gravatar.android.compose.get().pluginId
            implementationClass = "GravatarComposeConventionPlugin"
        }
        register("gravatarOpenApiGenerator") {
            id = libs.plugins.gravatar.openapi.generator.get().pluginId
            implementationClass = "GravatarOpenApiGeneratorConventionPlugin"
        }
    }
}
