val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val auth0JwksRsa: String by project
val auth0JavaJwt: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("maven-publish")
}

group = "net.freshplatform.ktor_server.firebase_app_check"
version = "0.0.1"

application {
    mainClass.set("${group}.FirebaseAppCheckKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("com.auth0:jwks-rsa:$auth0JwksRsa")
    implementation("com.auth0:java-jwt:$auth0JavaJwt")
//    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}


publishing {
    publications {
//        create<MavenPublication>("mavenJava") {
//            from(components["java"])
//        }
        create<MavenPublication>("jitpack") {
            from(components["java"])

            groupId = "com.github.freshtechtips"
            artifactId = "ktor-server-firebase-app-check"
            version = project.version.toString()
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "jitpack"
            setUrl("https://jitpack.io")
        }
    }
}