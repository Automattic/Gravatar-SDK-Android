apply(plugin = "maven-publish")
apply(plugin = "signing")

extra["contralSonatypeBearer"] = System.getenv("CENTRAL_SONATYPE_BEARER")
extra["signingKeyId"] = System.getenv("SIGNING_KEY_ID")
extra["signingKey"] = System.getenv("SIGNING_KEY")
extra["signingPassword"] = System.getenv("SIGNING_PASSWORD")

afterEvaluate {
    configure<PublishingExtension> {
        repositories {
            maven {
                val contralSonatypeBearer = extra["contralSonatypeBearer"] as? String
                if (contralSonatypeBearer == null || contralSonatypeBearer.isEmpty()) {
                    logger.warn("CENTRAL_SONATYPE_BEARER is not set.")
                }
                name = "mavenCentral"
                url = uri("https://central.sonatype.com/api/v1/publisher/deployments/download/")
                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = "Bearer $contralSonatypeBearer"
                }
                authentication {
                    create("header", HttpHeaderAuthentication::class)
                }
            }
//            maven {
//                name = "s3"
//                url = uri("s3://a8c-libs.s3.amazonaws.com/android")
//                credentials(AwsCredentials::class.java) {
//                    accessKey = System.getenv("AWS_ACCESS_KEY")
//                    secretKey = System.getenv("AWS_SECRET_KEY")
//                }
//            }
        }
        publications {
            val moduleArtifactId: String? = extra["artifactId"] as? String
            val sdkVersion: String by rootProject.extra
            create<MavenPublication>("mavenCentral") {
                from(components["release"])

                group = "com.gravatar"
                artifactId = moduleArtifactId

                pom {
                    name = "Gravatar Android SDK"
                    description = "The official Gravatar Android SDK"
                    url = "https://github.com/Automattic/Gravatar-SDK-Android"
                    licenses {
                        license {
                            name = "Mozilla Public License, Version 2.0"
                            url = "http://www.mozilla.org/MPL/2.0/index.txt"
                        }
                    }

                    scm {
                        connection = "scm:git:github.com:Automattic/Gravatar-SDK-Android.git"
                        developerConnection =
                            "scm:git:ssh://github.com:Automattic/Gravatar-SDK-Android.git"
                        url = "https://github.com/Automattic/Gravatar-SDK-Android"
                    }

                    developers {
                        developer {
                            id = "AdamGrzybkowski"
                            name = "Adam Grzybkowski"
                            email = "adam.grzybkowski@automattic.com"
                        }
                    }

                    organization {
                        name = "Gravatar.com"
                        url = "https://www.gravatar.com/"
                    }
                }
            }
        }
    }
}

//val isTagBuild: Boolean = System.getenv("BUILDKITE_TAG")?.isNotEmpty() == true
//
//tasks.withType<PublishToMavenRepository>().configureEach {
//    onlyIf {
//        val pubExt = checkNotNull(extensions.findByType(PublishingExtension::class.java))
//        when {
//            repository == pubExt.repositories.getByName("mavenCentral") && !isTagBuild -> false
//            else -> true
//        }
//    }
//}

configure<SigningExtension> {
    val signingKeyId = extra["signingKeyId"] as? String
    val signingKey = extra["signingKey"] as? String
    val signingKeyPassword = extra["signingPassword"] as? String
    useInMemoryPgpKeys(
        signingKeyId ?: "",
        signingKey ?: "",
        signingKeyPassword ?: "",
    )

    val pubExt = checkNotNull(extensions.findByType(PublishingExtension::class.java))
    val publication = pubExt.publications
    sign(publication)
}
