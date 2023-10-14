# Firebase App Check for Ktor server

AN experimental Ktor server plugin for configuring Firebase App Check easily and with simplicity.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Installation

Use this section to describe how to install your project. For example:

1. [Create a new ktor project](https://start.ktor.io/) or use existing one if you already have.
2. Add jitpack repository to your `build.gradle.kts`:
   ```groovy
   repositories {
    mavenCentral()
    maven {
        name = "jitpack"
        setUrl("https://jitpack.io")
    }
    }
   ```
3. Add the dependency:
   [![](https://jitpack.io/v/freshtechtips/ktor-server-firebase-app-check.svg)](https://jitpack.io/#freshtechtips/ktor-server-firebase-app-check)
    ```groovy
    dependencies {
        implementation("com.github.freshtechtips:ktor-server-firebase-app-check:0.0.1") // use the latest version
    }
    
    ```
4. Configure and install the plugin in the application module, 
Pass the following environment variables,
   go to your firebase project settings, in general tab

   * `FIREBASE_PROJECT_NUMBER` from the Project ID
   * `FIREBASE_PROJECT_ID` from the Project number

```kotlin
    install(FirebaseAppCheckPlugin) {
            configuration = FirebaseAppCheckPluginConfiguration(
                firebaseProjectNumber = System.getenv("FIREBASE_PROJECT_NUMBER"),
                firebaseProjectId = System.getenv("FIREBASE_PROJECT_ID"),
                overrideIsShouldVerifyToken = true,
                secureStrategy = FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes,
            ).apply {
                pluginMessages = FirebaseAppCheckMessages(
                    this,
                    appCheckIsNotDefinedResponse = mapOf(
                        "error" to "${this.firebaseAppCheckHeaderName} is required"
                    ),
                )
            }
        }
```

## Usage

Here's how to use the library:

First make sure to use the desire secure strategy in the plugin configuration when you install it, if you want to secure the whole api and the app,
or just a specific routes by surround them with `protectedRouteWithAppCheck {}`

```kotlin
routing {
        get("/") {
            call.respondText("Hello World! this route is not using app firebase app check")
        }
        protectRouteWithAppCheck {
            route("/products") {
                get("/1") {
                    call.respondText { "Product 1, Firebase app check" }
                }
                get("/2") {
                    call.respondText { "Product 2, Firebase app check" }
                }
            }
        }
        get("/test") {
            call.respondText { "Tis get test doesn't use firebase app check!" }
        }
        protectRouteWithAppCheck {
            post("/test") {
                call.respondText { "Tis post test is protected!" }
            }
        }
    }
```


### Features
List the key features of the library

please notice the library is still **experimental**

```markdown
## Features

- Easy to use and customizable
- Different secure strategies
- Caching and rate limiting for the public key of firebase app check
- Handle different errors
```

## Contributing

We welcome contributions!

Please follow these guidelines when contributing to our project. See [CONTRIBUTING.md](CONTRIBUTING.md) for more details.

## License

This project is licensed under the [MIT License](LICENSE) - see the [LICENSE](LICENSE) file for details.

Please notice the license can be changed, but it will still be open source.

## Acknowledgments

- Thanks to [Firebase App Check custom backend](https://firebase.google.com/docs/app-check/custom-resource-backend#other)
for updating the documentation and show us how to contact with their apis
- Thanks to Jetbrains for Kotlin, IntelliJ IDEA Community Edition, and Ktor for server
- Thanks to the open source community
- Thanks for [Auth0](https://developer.auth0.com/) and [Jwt io](https://jwt.io/) for the jwt libraries
and the debugger

[![Build Status](https://travis-ci.org/freshtechtips/ktor-server-firebase-app-check.svg?branch=master)](https://travis-ci.org/freshtechtips/ktor-server-firebase-app-check)
[![](https://jitpack.io/v/freshtechtips/ktor-server-firebase-app-check.svg)](https://jitpack.io/#freshtechtips/ktor-server-firebase-app-check)
