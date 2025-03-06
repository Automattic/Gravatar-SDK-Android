pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id ("org.jetbrains.dokka") version ("1.9.20")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://a8c-libs.s3.amazonaws.com/android")
            content {
                includeGroup("com.automattic")
                includeGroup("com.automattic.ucrop")
            }
        }
    }
}

rootProject.name = "gravatar"
include(":gravatar")
include(":demo-app")
include(":gravatar-ui")
include(":gravatar-quickeditor")
include(":uitestutils")
