plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ticktick2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ticktick2"
        minSdk = 34
        targetSdk = 34
        versionCode = 11
        versionName = "5.4"

        // 1 -> 1
        // 2 -> 2
        // 3 -> 3
        // 4 -> 4
        // 5 -> 4.1
        // 6 -> 4.2
        // 7 -> 5.0
        // 8 -> 5.1
        // 9 -> 5.2
        // 10 -> 5.3
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation("nl.dionsegijn:konfetti-xml:2.0.4")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("com.prolificinteractive:material-calendarview:1.4.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation ("com.google.code.gson:gson:2.8.8")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}