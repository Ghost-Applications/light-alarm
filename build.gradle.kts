plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")

    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.play.publisher)

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
    compileSdk = 33
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "cash.andrew.lightalarm"
        minSdk = 26
        targetSdk = 33

        val buildNumber: String by project
        versionCode = if (buildNumber.isBlank()) 1 else buildNumber.toInt()
        versionName = "yon"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    viewBinding {
        isEnabled = true
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
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    implementation(libs.material)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.timber)

    implementation(libs.paper)

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.truth)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
