plugins {
    val kotlinVersion = "1.7.20"
    id("com.android.application") version "7.2.0"
    id("kotlin-android") version kotlinVersion
    id("kotlin-kapt") version kotlinVersion
    id("com.google.gms.google-services") version "4.3.14"
    id("com.google.firebase.crashlytics") version "2.9.2"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.github.triplet.play") version "3.7.0"
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

repositories {
    google()
    mavenCentral()
    jcenter()
}

android {
    compileSdkVersion(33)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "cash.andrew.lightalarm"
        minSdkVersion(26)
        targetSdkVersion(33)

        val buildNumber: String by project
        versionCode = if (buildNumber.isBlank()) 1 else buildNumber.toInt()
        versionName = "三"
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
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")

    implementation("com.google.firebase:firebase-analytics:21.1.1")
    implementation("com.google.firebase:firebase-crashlytics:18.2.13")

    implementation("com.google.android.material:material:1.6.1")
    implementation("com.google.dagger:dagger:2.44")
    kapt("com.google.dagger:dagger-compiler:2.44")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("io.paperdb:paperdb:2.7.1")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("com.google.truth:truth:1.1.3")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
