plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktlint) apply false
}

extra.set("libVersion", property("libVersion") as String)

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
