val kotlinVersion = extra["kotlin.version"] as String
val ktorVersion = extra["ktor.version"] as String
val auth0JwksRsaVersion = extra["auth0JwksRsa.version"] as String
val auth0JavaJwtVersion = extra["auth0JavaJwt.version"] as String

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("maven-publish")
    id("java-library")
}

group = "net.freshplatform.ktor_server.firebase_app_check"
version = "0.0.4-dev"
description = "A Ktor server plugin for configuring Firebase App Check easily and with simplicity. It is not affiliated with Firebase or Google and may not be suitable for production use yet."

application {
    mainClass.set("${group}.FirebaseAppCheckKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("com.auth0:jwks-rsa:$auth0JwksRsaVersion")
    implementation("com.auth0:java-jwt:$auth0JavaJwtVersion")
//    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

val jitpackGroupId = "com.github.freshtechtips"

publishing {
    publications {
        create<MavenPublication>("jitpack") {
            from(components["java"])

            groupId = jitpackGroupId
            artifactId = "ktor-server-firebase-app-check"
            version = project.version.toString()
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "jitpack"
            setUrl("https://jitpack.io")
            content { includeGroup(jitpackGroupId) }
        }
    }
}