plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    kotlin("plugin.serialization") version "2.1.0"
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android") version "2.51"
    kotlin("kapt")
}

android {
    namespace = "roy.ij.baatcheet"
    compileSdk = 35

    defaultConfig {
        applicationId = "roy.ij.baatcheet"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "2.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false

            // ✅ Define production URLs
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://baatcheet-backend-jrndg.ondigitalocean.app/\""
            )
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://baatcheet-backend-jrndg.ondigitalocean.app/api/\""
            )
            buildConfigField(
                "String",
                "SOCKET_BASE_URL",
                "\"https://baatcheet-backend-jrndg.ondigitalocean.app\""
            )
        }
        debug {
            // ✅ Local/dev URLs
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://ij.dophera.xyz/\""
            )
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://ij.dophera.xyz/api/\""
            )
            buildConfigField(
                "String",
                "SOCKET_BASE_URL",
                "\"https://ij.dophera.xyz\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        disable += "NullSafeMutableLiveData"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.material.icons.extended)

    // Retrofit for networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines for asynchronous operations
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.lifecycle.runtime.compose)

    // Markwon for displaying Markdown
    implementation(libs.core)
    implementation(libs.ext.strikethrough)
    implementation(libs.ext.tables)
    implementation(libs.html)

    //socket.io
    implementation("io.socket:socket.io-client:2.1.0") {
        exclude (group= "org.json", module= "json")
    }

    // Room for local database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // SQLCipher for database encryption
    implementation(libs.android.database.sqlcipher)
    implementation(libs.androidx.sqlite.ktx)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // OkHttp logging (nice for debugging network calls)
    implementation(libs.logging.interceptor)

    // Zingx core
    implementation(libs.zxing.core)
    // ZXing embedded (camera scan)
    implementation(libs.zxing.android.embedded)

    //coil for image preview
    implementation(libs.coil.compose)

    // Lottie animations
    implementation(libs.lottie.compose)

    // Accompanist navigation animation
    implementation(libs.accompanist.navigation.animation)
}