import org.gradle.kotlin.dsl.extra

rootProject.name = "FirebaseAppCheck"
include(":example")

pluginManagement {
    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val ktorVersion = extra["ktor.version"] as String

        kotlin("jvm").version(kotlinVersion).apply(false)
        id("io.ktor.plugin").version(ktorVersion).apply(false)
        id("maven-publish").apply(false)
    }
}