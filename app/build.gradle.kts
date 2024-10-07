plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    id("org.openapi.generator") version "7.0.1"
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" // Add KSP plugin with the appropriate version
    id("org.owasp.dependencycheck") version "10.0.4"
}

openApiGenerate {
    inputSpec.set("$rootDir/openapi/sleradio.yml")
    generatorName.set("kotlin")
    library.set("jvm-retrofit2")
}

android {
    namespace = "com.armstrongindustries.jbradio"
    testNamespace = "com.armstrongindustries.mytestapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.armstrongindustries.jbradio"
        minSdk = 33
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
        //dataBinding = true
    }

    //testOptions {
    //    unitTests.all {
    //        it.useJUnitPlatform()
    //    }
   // }
}

dependencies {
    /**
     * Bouncy Castle Provider for JDK 18
     */
    implementation(libs.bcprov.jdk18on)

    /**
     * google gson library and converter
     */
    implementation(libs.google.gson)
    implementation(libs.converter.gson)

    /**
     * Retrofit
     */
    implementation(libs.retrofit2.retrofit)

    /**
     * Coil Image Loader
     */
    implementation(libs.coil)

    //implementation(libs.org.jetbrains.kotlin.kapt.gradle.plugin)
    //implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.core)

    //implementation(libs.mongodb.driver.kotlin.coroutine)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.datasource)
    implementation(libs.androidx.ui.android)
    implementation(libs.cronet.embedded)
    implementation(libs.androidx.room.common)


    /**
     * JUnit 5 & Testing Dependencies
     */
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}