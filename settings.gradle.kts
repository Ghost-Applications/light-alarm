rootProject.name = "Light Alarm"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id in arrayOf("kotlin-android", "kotlin-kapt", "kotlin-android-extensions")) {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }

            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }

            if (requested.id.id == "androidx.navigation.safeargs") {
                useModule("android.arch.navigation:navigation-safe-args-gradle-plugin:${requested.version}")
            }
        }
    }
}

