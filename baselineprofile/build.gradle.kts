plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.baselineprofile)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

android {
    namespace = "de.tobiasschuerg.weekview.baselineprofile"
    compileSdk = 37

    defaultConfig {
        // Macrobenchmark needs API 28; recording profiles needs API 33+ or a rooted device.
        minSdk = 28
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Drives the sample app; the library keeps the part of the profile that covers its own code.
    targetProjectPath = ":app"
}

baselineProfile {
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}
