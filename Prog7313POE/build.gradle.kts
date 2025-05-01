// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // make sure the version matches your kotlin("android") plugin version!
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.application")    apply false
    id("org.jetbrains.kotlin.android") apply false
    id("org.jetbrains.kotlin.kapt")    apply false
}