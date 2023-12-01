plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.dietin"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.dietin"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        compose = true
        mlModelBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    //permission
    implementation("com.google.accompanist:accompanist-permissions:0.18.0")

    //camera
    implementation("androidx.camera:camera-camera2:1.2.1")
    implementation("androidx.camera:camera-lifecycle:1.2.1")
    implementation("androidx.camera:camera-view:1.2.1")

    //coilImage
    implementation("io.coil-kt:coil-compose:2.2.2")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    //navigation
    implementation("androidx.navigation:navigation-compose:2.6.0")

    //horizontalpager
    implementation("androidx.compose.foundation:foundation:1.4.3")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1")

    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //tflite
    implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}