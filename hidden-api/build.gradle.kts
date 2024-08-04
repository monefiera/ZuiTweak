plugins {
    id("com.android.library")
}

android {
    namespace = "kr.stonecold.hidden_api"
    compileSdk = 34

    defaultConfig {
        minSdk = 33
    }
    signingConfigs {
        create("release") {
            storeFile = File(projectDir, "release-keystore.jks")
            storePassword = System.getenv("storePassword")
            keyAlias = System.getenv("keyAlias")
            keyPassword = System.getenv("keyPassword")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //noinspection UseTomlInstead
    annotationProcessor("dev.rikka.tools.refine:annotation-processor:4.4.0")
    //noinspection UseTomlInstead
    compileOnly("dev.rikka.tools.refine:annotation:4.4.0")
}