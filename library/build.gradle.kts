plugins {
    alias(libs.plugins.kotlin.multiplatform)
//    application
    id("maven-publish")
//    id("java-library")
}

val kotlinVersion = libs.versions.kotlin.get()
val ktorVersion = libs.versions.ktor.get()

group = "net.freshplatform"
version = libs.versions.library.get()
description =
    "A Ktor server plugin for configuring Firebase App Check easily and with simplicity. It is not affiliated with Firebase or Google and may not be suitable for production use yet."

//application {
//    mainClass.set("${group}.ktor_server.firebase_app_check.FirebaseAppCheckKt")
//}

kotlin {
    jvm()

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
//        val nativeMain by creating {
//            dependsOn(commonMain)
//        }
//        val nativeTest by creating {
//            dependsOn(nativeMain)
//        }
    }
}

repositories {
    mavenCentral()
}

//publishing {
//
//    val jitpackGroupId = "com.github.freshtechtips"
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