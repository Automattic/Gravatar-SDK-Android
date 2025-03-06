import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.openapitools.generator.gradle.plugin.OpenApiGeneratorPlugin
import org.openapitools.generator.gradle.plugin.extensions.OpenApiGeneratorGenerateExtension
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

class GravatarOpenApiGeneratorConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<OpenApiGeneratorPlugin>()

            extensions.configure<OpenApiGeneratorGenerateExtension> {
                generatorName.set("kotlin")
                inputSpec.set("${projectDir.path}/openapi/api-gravatar.json")
                outputDir.set("${layout.buildDirectory.asFile.get().absolutePath}/openapi")

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

            tasks.withType<GenerateTask>().configureEach {
                // Workaround for avoid the build error
                notCompatibleWithConfigurationCache(
                    "Incomplete support for configuration cache in OpenAPI Generator plugin.",
                )

                val buildPath = layout.buildDirectory.asFile.get().absolutePath

                // Move the generated code to the correct package and remove the generated folder
                doLast {
                    file("${projectDir.path}/src/main/java/com/gravatar/restapi").deleteRecursively()
                    file("$buildPath/openapi/src/main/kotlin/com/gravatar/restapi")
                        .renameTo(file("${projectDir.path}/src/main/java/com/gravatar/restapi"))
                    file("$buildPath/openapi").deleteRecursively()
                }

                // Format the generated code
                this.finalizedBy(project.tasks.named("ktlintFormat"))

                // Always run the task forcing the up-to-date check to return false
                outputs.upToDateWhen { false }
            }
        }
    }
}
