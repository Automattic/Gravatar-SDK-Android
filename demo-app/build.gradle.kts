import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.compose)
    // Ktlint
    alias(libs.plugins.ktlint)
    // Detekt
    alias(libs.plugins.detekt)
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
        minSdk = 23
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
                "DEMO_GRAVATAR_API_KEY",
                properties["demo-app.gravatar.api.key"]?.let { "\"$it\"" } ?: "null",
            )
            buildConfigField(
                "String",
                "DEMO_WORDPRESS_CLIENT_ID",
                "\"${properties["demo-app.wordpress.oauth.clientId"]?.toString() ?: ""}\"",
            )
            buildConfigField(
                "String",
                "DEMO_WORDPRESS_CLIENT_SECRET",
                "\"${properties["demo-app.wordpress.oauth.clientSecret"]?.toString() ?: ""}\"",
            )
            buildConfigField(
                "String",
                "DEMO_WORDPRESS_REDIRECT_URI",
                "\"${properties["demo-app.wordpress.oauth.redirectUri"]?.toString() ?: ""}\"",
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
        isCoreLibraryDesugaringEnabled = true

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
    coreLibraryDesugaring(libs.desugarJdk)

    // Demo App dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)

    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.coil.compose)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(project(":gravatar"))
    implementation(project(":gravatar-ui"))
    implementation(project(":gravatar-quickeditor"))

    debugImplementation(libs.androidx.compose.ui.tooling)

    // Unit Test dependencies
    testImplementation(libs.junit)
}
