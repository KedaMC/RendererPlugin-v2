import com.launchers_plugin.renderer.buildscript.buildEnvs
import com.launchers_plugin.renderer.buildscript.buildJsonValue
import com.launchers_plugin.renderer.buildscript.legacyManifest
import com.launchers_plugin.renderer.buildscript.nativePath
import com.launchers_plugin.renderer.buildscript.renderer
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.launchers_plugin.renderer.dsl")
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
}

android {
    namespace = "com.keda.ltw.renderer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.keda.ltw.renderer"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        resValues = true
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
        configureEach {
            resValue("string", "app_name", "LTW Renderer")
            applicationIdSuffix = ".ltw"

            // RendererPlugin V2 configuration.
            resValue("string", "config", buildJsonValue {
                renderer(
                    displayName = "LTW Renderer",
                    rendererId = "opengles3",
                    rendererGLPath = "libltw.so",
                    rendererEGLPath = nativePath("libltw.so"),
                    dlopenLibPaths = emptyList(),
                    env = buildEnvs {
                        normal("LIBGL_ES", "3")
                    },
                    minMCVer = null,
                    maxMCVer = null,
                )
            })

            // Legacy V1 metadata for launchers that do not yet read V2.
            manifestPlaceholders.putAll(legacyManifest {
                displayName = "LTW Renderer"
                rendererName = "LTW Renderer"
                rendererLib = "libltw.so"
                eglLib = "/libltw.so"
                minMCVer = ""
                maxMCVer = ""

                boatEnv {
                    put("LIBGL_ES", "3")
                    put("POJAV_RENDERER", "opengles3")
                }
                pojavEnv {
                    put("LIBGL_ES", "3")
                    put("POJAV_RENDERER", "opengles3")
                }
            })
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

dependencies {
    implementation(libs.androidx.annotation)
}
