plugins {
    val kotlinVersion = "1.3.71"
    id("com.android.application") version "3.6.2"
    id("kotlin-android") version kotlinVersion
    id("kotlin-kapt") version kotlinVersion
    id("com.google.gms.google-services") version "4.3.3"
    id("com.google.firebase.crashlytics") version "2.0.0-beta04"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("com.github.triplet.play") version "2.7.5"
    id("build-number")
    id("android-signing-config")
}

play {
    serviceAccountCredentials = file(properties["cash.andrew.mntrail.publishKey"] ?: "keys/publish-key.json")
    track = "internal"
}

repositories {
    google()
    mavenCentral()
    jcenter()
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        applicationId = "cash.andrew.lightalarm"
        minSdkVersion(26)
        targetSdkVersion(29)

        val buildNumber: String by project
        versionCode = if (buildNumber.isBlank()) 1 else buildNumber.toInt()
        versionName = "🍊🤡"
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

    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta4")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")

    implementation("com.google.firebase:firebase-analytics:17.3.0")
    implementation("com.google.firebase:firebase-crashlytics:17.0.0-beta04")

    implementation("com.google.android.material:material:1.1.0")
    implementation("com.google.dagger:dagger:2.27")
    kapt("com.google.dagger:dagger-compiler:2.27")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")

    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation("io.paperdb:paperdb:2.6")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("com.google.truth:truth:1.0.1")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
    kotlinOptions.jvmTarget = "1.8"
}
