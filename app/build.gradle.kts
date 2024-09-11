
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    id("de.mannodermaus.android-junit5") version "1.11.0.0"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources.pickFirsts.apply { add("META-INF/LICENSE.md")
        add("META-INF/LICENSE-notice.md")}
    }

    tasks.withType<Test> {
            useJUnitPlatform()
    }
}

dependencies {
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    //androidTestRuntimeOnly(libs.junit.platform.launcher)
    //androidTestImplementation(libs.junit.bom)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.gson)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    implementation(libs.flogger)
    implementation(libs.flogger.backend)
    androidTestImplementation(libs.androidx.espresso.core)
}