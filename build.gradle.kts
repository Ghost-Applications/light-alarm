import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  val kotlinVersion = "1.3.70"
  id("com.android.application") version "3.6.0"
  id("kotlin-android") version kotlinVersion
  id("kotlin-kapt") version kotlinVersion
  id("kotlin-android-extensions") version kotlinVersion
  id("com.github.ben-manes.versions") version "0.28.0"
}

repositories {
  google()
  mavenCentral()
  jcenter()
}

android {
  compileSdkVersion(29)
  buildToolsVersion("29.0.2")

  defaultConfig {
    applicationId = "cash.andrew.lightalarm"
    minSdkVersion(26)
    targetSdkVersion(29)
    versionCode = 1
    versionName = "1.0"
  }
  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"
      signingConfig = signingConfigs.getByName("debug")
      isMinifyEnabled = false
      isShrinkResources = false
      buildConfigField("boolean", "MOSHI_GENERATOR_ENABLED", "false")
    }
    getByName("release") {
      isMinifyEnabled = false
      isShrinkResources = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  sourceSets.all {
    java.srcDirs(file("src/$name/kotlin"))
  }
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  implementation("androidx.appcompat:appcompat:1.1.0")
  implementation("androidx.core:core-ktx:1.2.0")
  implementation("androidx.constraintlayout:constraintlayout:1.1.3")

  implementation("com.google.android.material:material:1.1.0")
  implementation("com.google.dagger:dagger:2.26")
  kapt("com.google.dagger:dagger-compiler:2.26")

  implementation("com.jakewharton.timber:timber:4.7.1")

  implementation("io.paperdb:paperdb:2.6")

  testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
  testImplementation("com.google.truth:truth:1.0.1")
}

tasks.withType < KotlinCompile > {
  kotlinOptions.jvmTarget = "1.8"
}
