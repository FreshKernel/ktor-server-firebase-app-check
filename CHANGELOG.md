# Changelog

All notable changes to this project will be documented in this file.

[//]: # (## [next])

## 0.0.5-dev
* Update the versions to use kotlin `1.9.21` and ktor `2.3.6`
* Prepare the library to be a KMP Library by using the Kotlin Multiplatform Gradle plugin
and add the source sets (`jvmMain` and `commonMain`)
* Share some
* Update `README` by add a status section
* Fix a few typos in the docs
* Update the `build.gradle.kts` to make it less specific to JVM, and remove unused gradle ktor plugin
* Fix the `group` of the dependency
* Update GitHub Main workflow
* Add `before-push.sh` script

## 0.0.4-dev
* The library is now dev state
* Improve the tests
* Fix typos

## 0.0.3-experimental
* **Breaking Change**: Now you don't need to pass the configuration class as a value, just add the properties directly
* **Breaking change**: The `FirebaseAppCheckPlugin` has been moved to the root `kotlin` folder
* Separate the `src` to a module
* Include the `example` in the `settings.gradle.kts`
* Update the `build.gradle.kts` of the `example`

## 0.0.2-experimental
* Rename the folder `examples` to `example` and use only one example project
* Use latest version of kotlin `1.9.20`
* Use the latest version of Gradle

## 0.0.1-experimental

* initial experimental release.