plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

group = "net.freshplatform"
version = libs.versions.library.get()
description =
    "A Ktor server plugin for configuring Firebase App Check easily and with simplicity. It is not affiliated with Firebase or Google and may not be suitable for production use yet."

kotlin {
    jvm()
//    linuxX64() it should be setup by who need it
//    macosArm64() it should be setup by who need it

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.server.core)
                implementation(libs.kotlinx.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.server.core)
                implementation(libs.auth0.java.jwt)
                implementation(libs.auth0.java.jwksRsa)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.ktor.server.test.jvm)
            }
        }
    }
}

//publishing {
//
//    val jitpackGroupId = "com.github.freshplatform"
//
//    publications {
//        create<MavenPublication>("jitpack") {
//            from(components["java"])
//
//            groupId = jitpackGroupId
//            artifactId = "ktor-server-firebase-app-check"
//            version = project.version.toString()
//        }
//    }
//
//    repositories {
//        maven {
//            name = "jitpack"
//            setUrl("https://jitpack.io")
//            content { includeGroup(jitpackGroupId) }
//        }
//    }
//}