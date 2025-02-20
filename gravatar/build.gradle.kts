import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    alias(libs.plugins.vanniktech.maven.publish)
}

val sdkVersion: String by rootProject.extra

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
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi.converter)
    implementation(libs.kotlinx.coroutines)
    ksp(libs.moshi.kotlin.codegen)

    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))
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
            "moshiCodeGen" to "true",
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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    coordinates(
        groupId = "com.gravatar",
        artifactId = "gravatar",
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
            developerConnection.set("scm:git:ssh://github.com:Automattic/Gravatar-SDK-Android.git")
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
