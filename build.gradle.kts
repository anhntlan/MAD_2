//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    id("com.android.application") version "7.4.2" apply false
//
//    // Add the dependency for the Google services Gradle plugin
//    id("com.google.gms.google-services") version "4.4.2" apply false
//
//}
//buildscript {
//    dependencies {
//        classpath ("com.google.gms:google-services:4.4.2")
//
//    }
//}after lỗi sdk35 => null exception
plugins {
    id("com.android.application") version "8.9.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
