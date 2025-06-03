plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "de.blinkt.openvpn"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        @Suppress("DEPRECATION")
        targetSdk = 35
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
            assets.srcDirs("src/main/assets", "build/ovpnassets")
        }
    }

    buildFeatures {
        aidl = true
        buildConfig = true
    }

    // Если у тебя есть папка res (иконки и т.п.), она автоматически подключится
    // Если есть jniLibs (.so), они должны лежать в src/main/jniLibs/
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.annotation:annotation:1.7.1")
}
