# Firebase App Check Example

A simple project that showcase how to use the Firebase App Check
library

## Table of Contents

- [Installation](#installation)

## Installation
1. Clone the repository
2. Make sure you have Java 11 as minimum, 17 is recommended
3. If you want to try this example, you have two options. Either
go to build.gradle.kts and remove the `mavenLocal()`
or if you already publish the library to your mavenLocal then
remove the jitpack repository
4. Pass the following environment variables,
go to your firebase project settings, in general tab

    * `FIREBASE_PROJECT_NUMBER` from the Project ID
    * `FIREBASE_PROJECT_ID` from the Project number
5. The routes:

    Get `/`: Unprotected route

    Get `/products`: Protected routes with two products `/1` and `/2`

    Get `/test`: Unprotected route

    Post `/test`: Protected route
6. Send request and pass `X-Firebase-AppCheck` to the header.
    you can get the token from firebase app check sdk in the client apps (Android, iOS, macOS, Web)
    Or if you just want to test real quick [try this expired token](https://pastebin.com/za2wW8cP).
   (
please notice in order to success the test you must generate the app token from client app that use the same project
)