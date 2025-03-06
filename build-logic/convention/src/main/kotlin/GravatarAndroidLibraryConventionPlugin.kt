import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.gravatar.configureDetekt
import com.gravatar.configureKotlinAndroid
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

class GravatarAndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<LibraryPlugin>()
            apply<KotlinAndroidPluginWrapper>()
            apply<KtlintPlugin>()
            apply<DetektPlugin>()

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
