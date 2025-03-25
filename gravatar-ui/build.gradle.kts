import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gravatar.maven.publish)
}

android {
    namespace = "com.gravatar.ui"

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets {
            configureEach {
                includes.from("GravatarUi.md")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(project(":gravatar"))

    testImplementation(libs.junit)
    testImplementation(project(":uitestutils"))

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
