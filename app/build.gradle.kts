plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" // Add KSP plugin with the appropriate version
    //id("org.owasp.dependencycheck") version "10.0.4"
    id("org.openapi.generator") version "7.0.1"
}

openApiGenerate {
    inputSpec.set("$rootDir/openapi/sleradio.yml")
    generatorName.set("kotlin")
    library.set("jvm-retrofit2")
}

//sourceSets {
//    getByName("main") {
//            java {
//                srcDir("$rootDir/build/generate-resources/main/src")
//        }
//    }
//}

android {
    namespace = "com.armstrongindustries.jbradio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.armstrongindustries.jbradio"
        minSdk = 30
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
        dataBinding = true
        viewBinding = true
    }

}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.converter.gson)
    implementation(libs.coil)
    implementation(libs.retrofit)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.ui.android)
    //testImplementation(libs.kotest.runner.junit5)
    //testImplementation(libs.androidx.junit)
    //androidTestImplementation(libs.kotest.runner.junit5)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}