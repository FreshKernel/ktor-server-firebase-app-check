val kotlinVersion = extra["kotlin.version"] as String
val ktorVersion = extra["ktor.version"] as String
val auth0JwksRsaVersion = extra["auth0JwksRsa.version"] as String
val auth0JavaJwtVersion = extra["auth0JavaJwt.version"] as String

plugins {
    kotlin("multiplatform")
    application
    id("maven-publish")
    id("java-library")
}

group = "net.freshplatform"
version = "0.0.5-dev"
description =
    "A Ktor server plugin for configuring Firebase App Check easily and with simplicity. It is not affiliated with Firebase or Google and may not be suitable for production use yet."

application {
    mainClass.set("${group}.ktor_server.firebase_app_check.FirebaseAppCheckKt")
}

kotlin {
    jvm {
        jvmToolchain(17)
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
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
                implementation("com.auth0:jwks-rsa:$auth0JwksRsaVersion")
                implementation("com.auth0:java-jwt:$auth0JavaJwtVersion")
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

publishing {

    val jitpackGroupId = "com.github.freshtechtips"

    publications {
        create<MavenPublication>("jitpack") {
            from(components["java"])

            groupId = jitpackGroupId
            artifactId = "ktor-server-firebase-app-check"
            version = project.version.toString()
        }
    }

    repositories {
        maven {
            name = "jitpack"
            setUrl("https://jitpack.io")
            content { includeGroup(jitpackGroupId) }
        }
    }
}