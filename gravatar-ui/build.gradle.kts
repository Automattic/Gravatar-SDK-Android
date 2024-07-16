import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id(libs.plugins.android.library.get().pluginId)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.publish.to.s3)
    // Ktlint
    alias(libs.plugins.ktlint)
    // Detekt
    alias(libs.plugins.detekt)
    // Roborazzi
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.gravatar.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        // targetSdkVersion has no effect for libraries. This is only used for the test APK
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    detekt {
        config.setFrom("${project.rootDir}/config/detekt/detekt.yml")
        source.setFrom("src")
        autoCorrect = false
        buildUponDefaultConfig = true
        parallel = false
    }
    buildFeatures {
        compose = true
    }
    composeCompiler {
        // Needed for Layout Inspector to be able to see all of the nodes in the component tree:
        // https://issuetracker.google.com/issues/338842143
        includeSourceInformation.set(true)
    }

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets {
            configureEach {
                includes.from("GravatarUi.md")
            }
        }
    }

    // Explicit API mode
    kotlin {
        explicitApi()
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                // -Pscreenshot to filter screenshot tests
                it.useJUnit {
                    if (project.hasProperty("screenshot")) {
                        includeCategories("com.gravatar.gravatar.ui.ScreenshotTests")
                    } else {
                        excludeCategories("com.gravatar.gravatar.ui.ScreenshotTests")
                    }
                }
            }
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugarJdk)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.ucrop)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(project(":gravatar"))

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Roborazzi
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.junit)
    debugImplementation(libs.androidx.compose.manifest)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "com.gravatar"
                artifactId = "gravatar-ui"
                // version is set by `publish-to-s3` plugin
            }
        }
    }
}
