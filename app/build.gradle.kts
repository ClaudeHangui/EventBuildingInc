@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.swenson.eventbuildinginc"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.swenson.eventbuildinginc"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas".toString()
                arguments["room.incremental"] = "true"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(libs.bundles.androidx.core.deps)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.jetpack.compose.deps)
    implementation(libs.bundles.coroutines.deps)
    implementation(libs.bundles.di.deps)
    implementation(libs.bundles.network.deps)
    implementation(libs.room.ktx)
    implementation("com.github.skydoves:sandwich:2.0.5")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    kapt(libs.hilt.compiler)
    kapt(libs.room.compiler)
    testImplementation(libs.bundles.local.test.deps)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.bundles.android.test.deps)
    debugImplementation(libs.bundles.debug.compose.deps)
}