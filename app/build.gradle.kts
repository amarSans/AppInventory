plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.tugasmobile.inventory"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tugasmobile.inventory"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md") // Mengecualikan file LICENSE.md
            excludes.add("META-INF/LICENSE-notice.md") // Jika ada file lain yang bermasalah
            excludes.add("META-INF/NOTICE.md") // Mengecualikan file NOTICE.md
            excludes.add("META-INF/DEPENDENCIES") // Mengecualikan file DEPENDENCIES jika diperlukan
        }
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
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.runner)
    implementation(libs.androidx.fragment.testing)
    implementation(libs.filament.android)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.screenshot.validation.junit.engine)
    implementation(libs.androidx.preference)
    implementation(libs.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.inline)
    androidTestImplementation(libs.mockito.kotlin)
    implementation(libs.glide)
    implementation(libs.androidx.work)
    implementation(libs.google.flexbox)
    implementation(libs.lottie)


}