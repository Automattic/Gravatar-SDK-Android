import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id(libs.plugins.android.library.get().pluginId)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.publish.to.s3)
    // Ktlint
    alias(libs.plugins.ktlint)
    // Detekt
    alias(libs.plugins.detekt)
    // Roborazzi
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.gravatar.quickeditor"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        val composeReportsDir = "reports/compose"
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
                "${project.rootDir}/compose_compiler_config.conf",
        )
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                project.layout.buildDirectory.get().dir(composeReportsDir).asFile.absolutePath,
        )
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                project.layout.buildDirectory.get().dir(composeReportsDir).asFile.absolutePath,
        )
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets {
            configureEach {
                includes.from("QuickEditor.md")
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
                        includeCategories("com.gravatar.uitestutils.ScreenshotTests")
                    } else {
                        excludeCategories("com.gravatar.uitestutils.ScreenshotTests")
                    }
                }
                it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
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
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.ucrop)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(project(":uitestutils"))
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "com.gravatar"
                artifactId = "gravatar-quickeditor"
                // version is set by `publish-to-s3` plugin
            }
        }
    }
}
