import com.android.build.api.dsl.DefaultConfig
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleGmsGoogleServices)
    alias(libs.plugins.googleFirebaseCrashlytics)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.android)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.kmpauth.firebase)
        }
        commonMain.apply {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                api(libs.gitlive.firebase.kotlin.crashlytics)
                implementation(libs.kmpauth.google) //Google One Tap Sign-In
                implementation(libs.kmpauth.firebase) //Integrated Authentications with Firebase
                implementation(libs.kermit)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
            }

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.vadhara7.mentorship_tree"
    compileSdk = libs.versions.android.compileSdk.get().toInt()


    defaultConfig {
        applicationId = "com.vadhara7.mentorship_tree"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        applyLocalPropsAsBuildConfig(project)

    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}


fun DefaultConfig.applyLocalPropsAsBuildConfig(project: Project) {
    val propsFile = project.rootProject.file("local.properties")
    val props = Properties().apply {
        if (propsFile.exists()) load(propsFile.inputStream())
    }
    props.forEach { (rawKey, rawValue) ->
        val key = rawKey.toString()
        val value = rawValue.toString()
        // Конвертуємо camelCase або інші формати в UPPER_SNAKE_CASE
        val fieldName = key
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")    // camelCase → camel_Case
            .replace(Regex("[^A-Za-z0-9_]"), "_")         // усе неалфанум → _
            .uppercase()                                 // → UPPER_SNAKE_CASE
        buildConfigField("String", fieldName, "\"$value\"")
    }
}
