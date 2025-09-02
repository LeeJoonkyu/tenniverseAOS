
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    kotlin("kapt")
}

android {
    namespace = "com.heejae.tenniverse"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.heejae.tenniverse"
        minSdk = 24
        targetSdk = 33
        versionCode = 16
        versionName = "2.0.10"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("/Users/junhyeong/StudioProjects/TenniverseAndroid/tenniverseKeyStore.jks")
            storePassword = "tenniverse"
            keyAlias = "tenniverseAlias"
            keyPassword = "tenniverse"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        dataBinding = true
    }
}

dependencies {

    val firebaseBom = platform("com.google.firebase:firebase-bom:32.3.1")
    val nav_version = "2.5.3"
    val hilt_version = "2.44"
    val room_version = "2.4.3"
    val lifecycle_version = "2.6.0-alpha03"
    val glide_version = "4.14.2"
    val paging_version = "3.1.1"
    val retrofit = "2.9.0"
    val okhttp = "4.9.0"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Room
    implementation ("androidx.room:room-runtime:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation ("androidx.navigation:navigation-ui-ktx:$nav_version")
    // Hilt
    implementation ("com.google.dagger:hilt-android:$hilt_version")
    kapt ("com.google.dagger:hilt-compiler:$hilt_version")

    // Firebase
    implementation(firebaseBom)
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
//    implementation("com.google.firebase:firebase-analytics-ktx")
    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")
//    // Firebase Realtime Database
//    implementation("com.google.firebase:firebase-database-ktx")
//    // Firebase Google Auth
//    implementation("com.google.firebase:firebase-auth:21.1.0")
    implementation("com.google.firebase:firebase-messaging-ktx")
    // Glide
    implementation("com.github.bumptech.glide:glide:$glide_version")
    annotationProcessor("com.github.bumptech.glide:compiler:$glide_version")

    // indicator
    implementation("com.tbuonomo:dotsindicator:5.0")

    // paging
    implementation("androidx.paging:paging-runtime-ktx:$paging_version")

    // swipe
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // browser
    implementation("androidx.browser:browser:1.3.0")

    // OkHttp3 & retrofit2
    implementation ("com.squareup.retrofit2:retrofit:$retrofit")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofit")
    implementation ("com.squareup.retrofit2:converter-scalars:$retrofit")
    implementation ("com.squareup.okhttp3:okhttp:$okhttp")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:$okhttp")
    implementation ("com.squareup.okhttp3:logging-interceptor:$okhttp")
}