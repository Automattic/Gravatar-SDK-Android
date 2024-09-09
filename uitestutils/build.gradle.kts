plugins {
    id(libs.plugins.android.library.get().pluginId)
    alias(libs.plugins.kotlin.android)

    // Ktlint
    alias(libs.plugins.ktlint)
    // Detekt
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.gravatar.uitestutils"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
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
}

dependencies {

    // Roborazzi
    api(libs.robolectric)
    implementation(libs.androidx.compose.junit)
    debugImplementation(libs.androidx.compose.manifest)
    implementation(libs.roborazzi)
    implementation(libs.roborazzi.compose)
    implementation(libs.roborazzi.junit.rule)
    implementation(libs.coil.test)
    implementation(libs.coil.compose)
}
