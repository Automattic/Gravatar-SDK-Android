import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    // Ktlint
    id("org.jlleitschuh.gradle.ktlint")

    // Detekt
    id("io.gitlab.arturbosch.detekt")

    // Publish artifact to S3
    id("com.automattic.android.publish-to-s3")

    // OpenApi Generator
    id("org.openapi.generator") version "7.4.0"
}

android {
    namespace = "com.gravatar"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    api("com.squareup.okhttp3:okhttp:4.12.0")
    val retrofitVersion = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk-android:1.13.9")
    testImplementation("io.mockk:mockk-agent:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
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
            "serializationLibrary" to "gson",
            "groupId" to "com.gravatar",
            "packageName" to "com.gravatar.restapi",
            "useCoroutines" to "true",
        ),
    )
    importMappings.set(
        mapOf(
            "DateTime" to "java.time.Instant",
        ),
    )

    typeMappings.set(
        mapOf(
            "DateTime" to "java.time.Instant",
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
