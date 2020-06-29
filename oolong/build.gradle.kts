import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.vanniktech.maven.publish") version "0.11.1"
}

repositories {
    jcenter()
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(deps.kotlin.stdlib.common)
                api(deps.kotlin.coroutines.core.common)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(deps.kotlin.test.common)
                implementation(deps.kotlin.test.annotations_common)
            }
        }

        val jvmMain by getting {
            dependencies {
                api(deps.kotlin.coroutines.core.jvm)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(deps.kotlin.test.jvm)
                implementation(deps.kotlin.test.junit5)
            }
        }

        val nativeMain by creating {
            dependencies {
                api(deps.kotlin.coroutines.core.native)
            }
        }

        val nativeTest by creating {
        }
    }

    targets {
        val unsupportedTargets = setOf(
            "android_arm32",
            "android_arm64",
            "android_x64",
            "android_x86",
            "linux_arm32_hfp",
            "linux_arm64",
            "linux_mips32",
            "linux_mipsel32",
            "mingw_x86",
            "wasm32"
        )

        presets
            .filterIsInstance<AbstractKotlinNativeTargetPreset<*>>()
            .filter { it.konanTarget.name !in unsupportedTargets }
            .forEach { preset ->
                targetFromPreset(preset, preset.name) {
                    compilations["main"].source(sourceSets["nativeMain"])
                    compilations["test"].source(sourceSets["nativeTest"])
                }
            }
    }
}

signing {
    val signingKey: String? by project
    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, null)
    }
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputDirectory = "$rootDir/docs"
        outputFormat = "gfm"

        multiplatform.apply {
            val global by creating {
                sourceLink {
                    path = "oolong/src/commonMain/kotlin"
                    url = "https://github.com/oolong-kt/oolong/tree/main/oolong/src/commonMain/kotlin"
                    lineSuffix = "#L"
                }
            }
            val common by creating {}
        }
    }
}
