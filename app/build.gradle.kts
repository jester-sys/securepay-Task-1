plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jaixlabs.securepay"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jaixlabs.securepay"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

     implementation ("androidx.biometric:biometric:1.2.0-alpha04")
 implementation ("androidx.security:security-crypto:1.1.0-alpha03")
 implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
 implementation ("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
 implementation ("androidx.room:room-runtime:2.6.0")
 annotationProcessor ("androidx.room:room-compiler:2.6.0")
 implementation ("com.squareup.retrofit2:retrofit:2.9.0")
 implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
}