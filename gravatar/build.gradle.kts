import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.openapi.generator)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    alias(libs.plugins.gravatar.maven.publish)
}

val sdkVersion: String by rootProject.extra

android {
    namespace = "com.gravatar"
    buildFeatures.buildConfig = true

    defaultConfig {
        buildConfigField("String", "SDK_VERSION", "\"$sdkVersion\"")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets {
            configureEach {
                includes.from("GravatarCore.md")
            }
        }
    }

    detekt {
        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            exclude("**/restapi/**") // Specify the directory to exclude
        }
    }
}

dependencies {
    api(libs.okhttp)
    implementation(libs.moshi)
    implementation(libs.kotlinx.coroutines)
    ksp(libs.moshi.kotlin.codegen)

    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))
}
