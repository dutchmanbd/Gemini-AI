@file:Suppress("DSL_SCOPE_VIOLATION")

import java.io.File
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.kotlin)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.devtools.ksp)

    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.ticonsys.geminiai"
    compileSdk = 34

    val localProperties = Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "local.properties")))
    }

    defaultConfig {
        applicationId = "com.ticonsys.geminiai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        buildConfigField("String", "API_KEY", localProperties.getProperty("API_KEY"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.bundles.androidx)

    ksp(libs.androidx.hilt.compiler)
    kapt(libs.hilt.android.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.androidx.hilt)
    implementation(libs.hilt.android)
    implementation(libs.bundles.navigation)


    implementation(libs.compose.navigation.core)
    ksp(libs.compose.navigation.ksp)

    implementation(libs.generative.ai)
    implementation(libs.collections.immutable)
//    implementation(libs.highlightjs.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.espresso)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.bundles.composeTest)
}