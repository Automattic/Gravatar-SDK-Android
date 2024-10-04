import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id(libs.plugins.android.library.get().pluginId)
    alias(libs.plugins.kotlin.android)
    // Ktlint
    alias(libs.plugins.ktlint)
    // Detekt
    alias(libs.plugins.detekt)
    // Publish artifact to S3
    alias(libs.plugins.publish.to.s3)
    // OpenApi Generator
    alias(libs.plugins.openapi.generator)
}

val sdkVersion = providers.exec {
    commandLine("git", "describe", "--tags", "--abbrev=0")
}.standardOutput.asText.get().trim()

android {
    namespace = "com.gravatar"
    compileSdk = 34
    buildFeatures.buildConfig = true

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "SDK_VERSION", "\"$sdkVersion\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
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
        parallel = true
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

    // Explicit API mode
    kotlin {
        explicitApi()
    }
}

dependencies {
    api(libs.okhttp)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi.converter)
    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.kotlinx.coroutines.test)
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "com.gravatar"
                artifactId = "gravatar"
                // version is set by `publish-to-s3` plugin
            }
        }
    }
}

openApiGenerate {
    generatorName = "kotlin"
    inputSpec = "${projectDir.path}/openapi/api-gravatar.json"
    outputDir = "${layout.buildDirectory.asFile.get().absolutePath}/openapi"

    // Use the custom templates if they are present. If not, the generator will use the default ones
    templateDir.set("${projectDir.path}/openapi/templates")

    // Set the generation configuration options
    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "serializationLibrary" to "moshi",
            "groupId" to "com.gravatar",
            "packageName" to "com.gravatar.restapi",
            "useCoroutines" to "true",
        ),
    )
    importMappings.set(
        mapOf(
            "DateTime" to "String",
        ),
    )

    typeMappings.set(
        mapOf(
            "DateTime" to "String",
        ),
    )

    // We only want the apis and models, not the "infrastructure" folder
    // See: https://github.com/OpenAPITools/openapi-generator/issues/6455
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
        ),
    )
}

tasks.openApiGenerate {
    // Workaround for avoid the build error
    notCompatibleWithConfigurationCache("Incomplete support for configuration cache in OpenAPI Generator plugin.")

    val buildPath = layout.buildDirectory.asFile.get().absolutePath

    // Move the generated code to the correct package and remove the generated folder
    doLast {
        file("${projectDir.path}/src/main/java/com/gravatar/restapi").deleteRecursively()
        file("$buildPath/openapi/src/main/kotlin/com/gravatar/restapi")
            .renameTo(file("${projectDir.path}/src/main/java/com/gravatar/restapi"))
        file("$buildPath/openapi").deleteRecursively()
    }

    // Format the generated code
    this.finalizedBy(tasks.ktlintFormat.get().path)

    // Always run the task forcing the up-to-date check to return false
    outputs.upToDateWhen { false }
}
