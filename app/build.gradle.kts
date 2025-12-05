import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
}


        android {
            namespace = "com.weatherforecast"
            compileSdk = 36

            defaultConfig {
                applicationId = "com.weatherforecast"
                minSdk = 26
                targetSdk = 36
                versionCode = 1
                versionName = "1.0"
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                // 🔥 Load API key from local.properties safely
                val apiKey: String = gradleLocalProperties(rootDir,providers)
                    .getProperty("OPENWEATHER_API_KEY") ?: ""

                buildConfigField(
                    "String",
                    "OPENWEATHER_API_KEY",
                    "\"$apiKey\""
                )
            }

            buildFeatures {
                compose = true
                buildConfig = true
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            kotlinOptions {
                jvmTarget = "17"
            }
        }

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
// Accompanist for permissions
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.compose.animation:animation:1.10.0")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.compose.material3:material3:1.4.0") // or latest stable version
    implementation("androidx.room:room-ktx:2.8.4")


// Compose UI and tooling
    implementation("androidx.compose.ui:ui:1.10.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.10.0")
    implementation(libs.androidx.room.ktx)
    debugImplementation("androidx.compose.ui:ui-tooling:1.10.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)

    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.coil.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
