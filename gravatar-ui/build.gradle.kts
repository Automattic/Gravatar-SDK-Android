import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.automattic.android.publish-to-s3")

    // Ktlint
    id("org.jlleitschuh.gradle.ktlint")

    // Detekt
    id("io.gitlab.arturbosch.detekt")

    // Roborazzi
    id("io.github.takahirom.roborazzi")
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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.github.yalantis:ucrop:2.2.8")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-svg:2.5.0")
    implementation(project(":gravatar"))

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.2")

    // Roborazzi
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.6.7")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.7")
    testImplementation("io.github.takahirom.roborazzi:roborazzi:1.15.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-compose:1.15.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:1.15.0")
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
