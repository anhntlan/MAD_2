//import jdk.tools.jlink.resources.plugins
import org.apache.commons.logging.LogFactory.release

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")

}


android {
    namespace = "com.example.hipenjava"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hipenjava"
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
}
//
//dependencies {
//
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    implementation(libs.firebase.database)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//
//
//    implementation ("com.google.firebase:firebase-database:21.0.0")
//
//
//    // Import the Firebase BoM
//    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
//
//    // When using the BoM, you don't specify versions in Firebase library dependencies
//
//    // Add the dependency for the Firebase SDK for Google Analytics
//    implementation("com.google.firebase:firebase-analytics")
//
//    // TODO: Add the dependencies for any other Firebase products you want to use
//    // See https://firebase.google.com/docs/android/setup#available-libraries
//    // For example, add the dependencies for Firebase Authentication and Cloud Firestore
//    implementation("com.google.firebase:firebase-auth")
//    implementation("com.google.firebase:firebase-firestore")
//    //com.google.firebase:firebase-database
//}
dependencies {
    // AndroidX Core & UI
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha13")

    // Firebase (BOM auto-manages versions)
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    implementation("com.cloudinary:cloudinary-android:2.3.1")


}

