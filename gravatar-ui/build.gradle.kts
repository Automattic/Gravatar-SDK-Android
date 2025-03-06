import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish)
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
                        includeCategories("com.gravatar.uitestutils.ScreenshotTests")
                    } else {
                        excludeCategories("com.gravatar.uitestutils.ScreenshotTests")
                    }
                }
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

val sdkVersion: String by rootProject.extra

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    coordinates(
        groupId = "com.gravatar",
        artifactId = "gravatar-ui",
        version = sdkVersion,
    )

    pom {
        name.set("Gravatar Android SDK")
        description.set("The official Gravatar Android SDK")
        url.set("https://github.com/Automattic/Gravatar-SDK-Android")
        licenses {
            license {
                name.set("Mozilla Public License, Version 2.0")
                url.set("http://www.mozilla.org/MPL/2.0/index.txt")
            }
        }

        scm {
            connection.set("scm:git:github.com:Automattic/Gravatar-SDK-Android.git")
            developerConnection.set("scm:git:https://github.com:Automattic/Gravatar-SDK-Android.git")
            url.set("https://github.com/Automattic/Gravatar-SDK-Android")
        }

        developers {
            developer {
                id.set("AdamGrzybkowski")
                name.set("Adam Grzybkowski")
                email.set("adam.grzybkowski@automattic.com")
            }
        }

        organization {
            name.set("Gravatar.com")
            url.set("https://www.gravatar.com/")
        }
    }

    signAllPublications()
}
