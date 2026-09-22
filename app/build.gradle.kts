import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
}
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.jenil.f1comp"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.jenil.f1comp"
        minSdk = 26
        targetSdk = 37
        versionCode = 3
        versionName = "1.2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val secretApiKey = System.getenv("CHAT_API_KEY")
            ?: localProperties.getProperty("CHAT_API_KEY")
            ?: ""
        buildConfigField("String", "CHAT_API_KEY", "\"$secretApiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11) // Or 21
    }
    buildFeatures {
        compose = true
        buildConfig = true

    }
}

dependencies {


    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.appcompat)

    // Credential MaNGER
    //noinspection LoginCredentials,UseTomlInstead
    implementation("androidx.credentials:credentials:1.6.0")
    //noinspection LoginCredentials,UseTomlInstead
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    //noinspection LoginCredentials,UseTomlInstead
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.1")


    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)


    // Google Auth
    implementation(libs.googleid)

    //Image
    implementation(libs.coil.compose)

    //Vico for Charts
    //noinspection UseTomlInstead
    implementation("com.patrykandpatrick.vico:compose:3.3.1")

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    //Haze
    implementation(libs.haze)
    implementation(libs.haze.materials)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Parsing
    implementation(libs.jsoup)

    // MockK (For faking Repositories and DAOs)
    testImplementation(libs.mockk)

    // Coroutines Testing
    testImplementation(libs.kotlinx.coroutines.test)

    // Turbine (For testing StateFlows easily)
    testImplementation(libs.turbine)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}