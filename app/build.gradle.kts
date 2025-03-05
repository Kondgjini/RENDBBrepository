plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.rendbb"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rendbb"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1") // Latest stable version
    implementation("com.google.android.material:material:1.12.0") // Updated
    implementation("androidx.activity:activity:1.7.2") // Latest stable version
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Latest stable version
    implementation("androidx.recyclerview:recyclerview:1.3.1") // Updated to latest
    implementation ("androidx.preference:preference:1.1.1")

    testImplementation("junit:junit:4.13.2") // Latest stable version
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Latest stable version
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Latest stable version
}

