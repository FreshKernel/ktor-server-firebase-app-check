plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

val kotlinVersion = libs.versions.kotlin.get()
val ktorVersion = libs.versions.ktor.get()

group = "net.freshplatform"
version = libs.versions.library.get()
description =
    "A Ktor server plugin for configuring Firebase App Check easily and with simplicity. It is not affiliated with Firebase or Google and may not be suitable for production use yet."

kotlin {
    jvm()
    linuxX64()
    macosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation(libs.kotlinx.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
                implementation(libs.auth0.java.jwt)
                implementation(libs.auth0.java.jwksRsa)
//                implementation("io.ktor:ktor-server-auth-jwt-jvm")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
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