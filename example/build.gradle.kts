val kotlinVersion = extra["kotlin.version"] as String
val ktorVersion = extra["ktor.version"] as String
val logbackVersion = extra["logback.version"] as String
val auth0JwksRsaVersion = extra["auth0JwksRsa.version"] as String
val auth0JavaJwtVersion = extra["auth0JavaJwt.version"] as String

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "net.freshplatform"
version = "0.0.1"

application {
    mainClass.set("net.freshplatform.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
//    mavenLocal()
    val jitpackGroupId = "com.github.freshtechtips"
    maven {
        name = "jitpack"
        setUrl("https://jitpack.io")
        content { includeGroup(jitpackGroupId) }
    }
}

dependencies {
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-compression-jvm")
    implementation("io.ktor:ktor-server-caching-headers-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

    implementation(project(":library"))
//    implementation("com.github.freshtechtips:ktor-server-firebase-app-check:v0.0.3-experimental")
}
