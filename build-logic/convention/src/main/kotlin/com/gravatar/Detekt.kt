package com.gravatar

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureDetekt() {
    configure<DetektExtension> {
        config.setFrom("${project.rootDir}/config/detekt/detekt.yml")
        source.setFrom("src")
        autoCorrect = false
        buildUponDefaultConfig = true
        parallel = true
    }
}
