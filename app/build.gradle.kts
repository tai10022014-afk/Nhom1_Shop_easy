import java.util.Properties

plugins {
    id("com.google.devtools.ksp")
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Ký bản Release: tạo file keystore.properties ở thư mục gốc (xem keystore.properties.example)
    val keystoreProps = Properties().apply {
        val f = rootProject.file("keystore.properties")
        if (f.exists()) f.inputStream().use { load(it) }
    }
    signingConfigs {
        if (keystoreProps.isNotEmpty()) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            // Bật R8 (thu nhỏ + làm rối mã) cho bản phát hành; luật giữ lớp nằm ở src/main/keepRules/rules.keep
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // THÊM MỚI: bật View Binding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("io.coil-kt:coil:2.7.0")

    // THÊM MỚI: LiveData cho ViewModel
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // Phát hiện rò rỉ bộ nhớ (chỉ bản debug, không vào APK/AAB Release)
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}