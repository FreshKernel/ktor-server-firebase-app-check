import org.gradle.kotlin.dsl.extra

rootProject.name = "FirebaseAppCheck"
include(":example", ":library")

pluginManagement {

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
//        val ktorVersion = extra["ktor.version"] as String

        kotlin("multiplatform") version(kotlinVersion) apply(false)
//        kotlin("jvm") version(kotlinVersion) apply(false)
//        id("io.ktor.plugin") version(ktorVersion) apply(false)
        id("org.jetbrains.kotlin.plugin.serialization") version(kotlinVersion) apply(false) // For the `example`
    }
}