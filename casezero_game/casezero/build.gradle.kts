plugins {
    id("com.android.application")
}

android {
    namespace = "com.casezero.lastwitness"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.casezero.lastwitness"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    androidResources {
        noCompress += listOf("wav", "json", "png")
    }

    packaging {
        resources {
            excludes += setOf("META-INF/DEPENDENCIES", "META-INF/LICENSE*", "META-INF/NOTICE*")
        }
    }
}
