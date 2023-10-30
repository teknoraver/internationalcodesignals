plugins {
    id("com.android.application")
}

android {
    namespace = "net.teknoraver.ics"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "net.teknoraver.ics"
        minSdk = 21
        targetSdk = 34
        versionCode = 21
        versionName = "1.7"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
