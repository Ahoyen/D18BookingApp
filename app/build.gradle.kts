plugins {
//    alias(libs.plugins.android.application)
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ptit.aird18bookingapp"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.ptit.aird18bookingapp"
        minSdk = 24
        targetSdk = 34
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
    packaging {
        resources {
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/LICENSE.md" // cũng thường bị lỗi tương tự
            excludes += "com/j256/ormlite/core/README.txt"
            excludes += "com/j256/ormlite/core/LICENSE.txt"
        }
    }

}

dependencies {
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)

    // mapbox
//    implementation(libs.mapboxsdk)

    // osmdroid
//    implementation(libs.osmdroid)

    // rounded image
//    implementation(libs.roundedimageview)
    // chart
//    implementation(libs.mpandroidchart)


    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // osmdroid
    implementation("org.osmdroid:osmdroid-android:6.1.11")

    //    implementation ("org.osmdroid:osmdroid-geopackage:6.1.16")

    // rounded image
    implementation("com.makeramen:roundedimageview:2.3.0")


    // firebase
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.google.firebase:firebase-database:20.3.0")

    implementation ("com.github.d-max:spots-dialog:1.1@aar")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.stripe:stripe-android:20.32.0")

    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation ("androidx.core:core-ktx:1.9.0")

    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")


}

