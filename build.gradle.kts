// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext {
        compose_version = "1.5.15" // or latest stable
        hilt_version = "2.45"
        kotlin_version = "1.9.10"
    }
}


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}