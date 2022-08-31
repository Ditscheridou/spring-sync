plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.7.10"
    kotlin("native.cocoapods")
}

group = "de.jds"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    iosArm64 {
        binaries {
            framework {
                baseName = "library"
            }
        }
        cocoapods {
            summary = "Diff Librabry for ios"
            homepage = "https://github.com/onmyway133/DeepDiff"
            pod("DeepDiff")
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":spring-sync-core:shadowstores"))
                implementation(project(":diffi"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
            }

        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.apache.commons:commons-lang3:3.12.0")
                implementation("com.googlecode.java-diff-utils:diffutils:1.2.1")
            }
        }
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
        val iosArm64Main by getting {
            dependencies {
            }
        }
        val iosArm64Test by getting
    }
}