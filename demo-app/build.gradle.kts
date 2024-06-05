import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")

    // Ktlint
    id("org.jlleitschuh.gradle.ktlint")

    // Detekt
    id("io.gitlab.arturbosch.detekt")
}

fun localProperties(): Properties {
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }
    return localProperties
}

android {
    namespace = "com.gravatar.demoapp"
    compileSdk = 34
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.gravatar.demoapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        localProperties().let { properties ->
            buildConfigField(
                "String",
                "DEMO_EMAIL",
                "\"${properties["demo-app.email"]?.toString() ?: "gravatar@automattic.com"}\"",
            )
            buildConfigField(
                "String",
                "DEMO_WORDPRESS_BEARER_TOKEN",
                "\"${properties["demo-app.wordPressBearerToken"]?.toString() ?: ""}\"",
            )
            buildConfigField(
                "String",
                "DEMO_GRAVATAR_API_KEY",
                properties["demo-app.gravatar.api.key"]?.let { "\"$it\"" } ?: "null",
            )
        }
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
    }
    buildFeatures {
        compose = true
    }
    composeCompiler {
        // Enable 'strong skipping'
        // https://medium.com/androiddevelopers/jetpack-compose-strong-skipping-mode-explained-cbdb2aa4b900
        enableStrongSkippingMode.set(true)
        // Needed for Layout Inspector to be able to see all of the nodes in the component tree:
        // https://issuetracker.google.com/issues/338842143
        includeSourceInformation.set(true)
    }
    detekt {
        config.setFrom("${project.rootDir}/config/detekt/detekt.yml")
        source.setFrom("src")
        autoCorrect = false
        buildUponDefaultConfig = true
        parallel = false
    }
}

dependencies {
    // Demo App dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation(project(":gravatar"))
    implementation(project(":gravatar-ui"))

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")

    // Unit Test dependencies
    testImplementation("junit:junit:4.13.2")

    // Android Test dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
