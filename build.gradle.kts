plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.gradle.plugin)

    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.play.publisher)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.version.check)

    id("build-number")
    id("android-signing-config")
}

play {
    serviceAccountCredentials.set(
        rootProject.file(properties["cash.andrew.lightalarm.publishKey"] ?: "keys/publish-key.json")
    )
    track.set("internal")
    defaultToAppBundles.set(true)
}

android {
    namespace = "cash.andrew.lightalarm"
    compileSdk = 35

    defaultConfig {
        applicationId = "cash.andrew.lightalarm"
        minSdk = 26
        targetSdk = 35

        val buildNumber: String by project
        versionCode = if (buildNumber.isBlank()) 1 else buildNumber.toInt()
        versionName = "roku"
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(kotlin("stdlib"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.dynamicanimation)

    implementation(platform("com.google.firebase:firebase-bom:${libs.versions.firebase.get()}"))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)

    implementation(libs.material)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.timber)

    implementation(libs.paper)

    implementation(libs.kpermissions)

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.truth)
}
