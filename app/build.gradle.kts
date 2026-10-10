plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Adds the non-minified release build that :baselineprofile drives to record the library's profile.
    alias(libs.plugins.baselineprofile)
}

val libVersion = rootProject.extra["libVersion"] as String

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

android {
    compileSdk = 37

    defaultConfig {
        applicationId = "de.tobiasschuerg.weekview.sample"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = libVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            // Sample app only: signed with the debug key so profile generation can install it.
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    namespace = "de.tobiasschuerg.weekview.sample"
}

dependencies {
    implementation(project(":library"))

    // Compose BOM
    implementation(platform(libs.compose.bom))

    // Compose dependencies
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)

    // Compose debugging tools
    debugImplementation(libs.compose.ui.tooling)
}
