plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.gradle.plugin)

    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)

    id("build-number")
    id("android-signing-config")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

android {
    namespace = "cash.andrew.lightalarm"
    compileSdk = 36

    defaultConfig {
        applicationId = "rocks.ghostapps.lightalarm"
        minSdk = 26
        targetSdk = 36

        val buildNumber: String by project
        versionCode = if (buildNumber.isBlank()) 1 else buildNumber.toInt()
        versionName = "BackOnTheTrain"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("keys/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val keystoreLocation: String by project
            val keystorePassword: String by project
            val storeKeyAlias: String by project
            val aliasKeyPassword: String by project

            storeFile = file(keystoreLocation)
            storePassword = keystorePassword
            keyAlias = storeKeyAlias
            keyPassword = aliasKeyPassword
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
        aidl = false
        buildConfig = true
        compose = false
        prefab = false
        renderScript = false
        resValues = false
        shaders = false
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.dynamicanimation)
    implementation(libs.androidx.datastore)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.material)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.timber)
    implementation(libs.kpermissions)

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.truth)
}
