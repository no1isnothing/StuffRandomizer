
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.devtools.ksp) // faster/newer alternative to kapt
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.thebipolaroptimist.stuffrandomizer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.thebipolaroptimist.stuffrandomizer"
        minSdk = 31
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    packaging {
        resources.pickFirsts.apply { add("META-INF/LICENSE.md")
        add("META-INF/LICENSE-notice.md")}
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    annotationProcessor(libs.room.compiler)

    ksp(libs.hilt.android.compiler)
    ksp(libs.room.compiler)

    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.gson)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    implementation(libs.flogger)
    implementation(libs.flogger.backend)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.hilt.navigation.compose)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.test.manifest.v154)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(composeBom)

    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(composeBom)
    testImplementation(kotlin("test"))
}