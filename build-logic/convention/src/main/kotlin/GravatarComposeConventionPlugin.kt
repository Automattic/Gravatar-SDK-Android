import com.android.build.gradle.LibraryExtension
import io.github.takahirom.roborazzi.RoborazziPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradleSubplugin

class GravatarComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<RoborazziPlugin>()
            apply<ComposeCompilerGradleSubplugin>()

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
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
            }

            extensions.configure<ComposeCompilerGradlePluginExtension> {
                val composeReportsDir = "reports/compose"
                reportsDestination = project.layout.buildDirectory.get().dir(composeReportsDir).asFile
                metricsDestination = project.layout.buildDirectory.get().dir(composeReportsDir).asFile
                stabilityConfigurationFile = project.file("${project.rootDir}/compose_compiler_config.conf")
            }
        }
    }
}
