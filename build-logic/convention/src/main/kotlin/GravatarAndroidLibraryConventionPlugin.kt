import com.android.build.gradle.LibraryExtension
import com.gravatar.configureDetekt
import com.gravatar.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class GravatarAndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "org.jlleitschuh.gradle.ktlint")
            apply(plugin = "io.gitlab.arturbosch.detekt")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.apply {
                    targetSdk = 34
                    consumerProguardFiles("consumer-rules.pro")
                }
                configureBuildTypes()
            }
            configureDetekt()
        }
    }

    private fun LibraryExtension.configureBuildTypes() {
        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
        }
    }
}
