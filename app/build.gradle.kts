plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val libVersion: String by rootProject.extra

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "de.tobiasschuerg.weekview.sample"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = libVersion
        multiDexEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
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
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.kotlin.stdlib)

    // Compose BOM
    implementation(platform(libs.compose.bom))

    // Compose dependencies
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose debugging tools
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
