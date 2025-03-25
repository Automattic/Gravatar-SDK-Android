import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate

class MavenPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.vanniktech.maven.publish")

            extensions.configure<MavenPublishBaseExtension> {
                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
                signAllPublications()

                val sdkVersion: String by rootProject.extra

                coordinates(
                    groupId = "com.gravatar",
                    artifactId = name,
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
            }
        }
    }
}
