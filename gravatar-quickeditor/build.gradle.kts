import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gravatar.maven.publish)
}

android {
    namespace = "com.gravatar.quickeditor"

    defaultConfig {
        minSdk = 23
    }

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets {
            configureEach {
                includes.from("QuickEditor.md")
            }
        }
    }
}

dependencies {
    implementation(project(":gravatar"))
    implementation(project(":gravatar-ui"))

    implementation(libs.androidx.browser)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.security.crypto.datastore)
    implementation(libs.androidx.startup)

    implementation(libs.coil.compose)
    implementation(libs.ucrop)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.adaptive)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.composables.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.mockwebserver)
    testImplementation(project(":uitestutils"))
}
