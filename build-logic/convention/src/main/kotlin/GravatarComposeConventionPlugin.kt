import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class GravatarComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "io.github.takahirom.roborazzi")

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.15"
                }
                testOptions {
                    unitTests {
                        // For Roborazzi
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
                            it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
                        }
                    }
                }
                tasks.withType<KotlinCompile>().configureEach {
                    kotlinOptions {
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
                }
            }
        }
    }
}
