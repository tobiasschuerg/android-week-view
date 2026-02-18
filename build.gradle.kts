plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktlint) apply false
}

val libVersion: String by extra {
    val result = providers.exec {
        commandLine("git", "describe", "--tags", "--abbrev=0")
        isIgnoreExitValue = true
    }
    val exitCode = result.result.get().exitValue
    val tag = result.standardOutput.asText.get().trim()
    if (exitCode == 0 && tag.isNotEmpty()) {
        if (tag.startsWith("v")) tag.substring(1) else tag
    } else {
        "0.0.0"
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
