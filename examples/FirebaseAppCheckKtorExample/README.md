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
6. Send request and set the header `X-Firebase-AppCheck` with the app check token (jwt).
    you can get the token from firebase app check sdk in the client apps (Android, iOS, macOS, Web)
    Or if you just want to test real quick [try this expired token](https://pastebin.com/za2wW8cP).
   (
please notice in order to success the test you must generate the app token from client app that use the same project
)
   
Token for testing purposes:
```
eyJraWQiOiJ2Yy1sVEEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxOjgwMjA4OTE0MjU1OTphbmRyb2lkOjI2ZDhjMDA3ZGVkMDNmODQyYTg4MmEiLCJhdWQiOlsicHJvamVjdHNcLzgwMjA4OTE0MjU1OSIsInByb2plY3RzXC9teW5vdGVzLWViNzE3Il0sInByb3ZpZGVyIjoiZGVidWciLCJpc3MiOiJodHRwczpcL1wvZmlyZWJhc2VhcHBjaGVjay5nb29nbGVhcGlzLmNvbVwvODAyMDg5MTQyNTU5IiwiZXhwIjoxNjk3MTM0NDg3LCJpYXQiOjE2OTcxMzA4ODcsImp0aSI6InZLZERfNTRhQ2tzVmpHV0xBN3d1TjZmWlFUQWRYZzRBWGJhYVBzRUZDV0EifQ.H_LGsCe5I-Z2uAgYU1isDmxQ-6PecdmjEqvkrZp9AWthNhsiMdlVYjUe2DaSmt3lhIlwCJyCh2YooOLvSlFAvdx5n__kB5O5C9Fw-Vw-zjSTOAi6lNB0hi8OEkIJhNgw2b_UipeVFd1I6ICkCdV93Ewr-clv-eDeMIg_b8vr3w6HtypZDVu3hAl6BjfxY9r7cm5eBmHGnOxwb1-flSKRJdBmrh4Bm0_imaDPSHw_rUwCUXHOAM-QfdQ-D4C15L_IJH4X6kT7nm8GMj47rQjr1d6CQZbW3xoIsTJvnpreOR1xyiHZiLydj1cwPt6r2DfmjRL6-tFs2u8c72CcoqQ4hhsJE9ZSk1BHXpnGw6t5PLPWmk-K7wCrn49U20SYsbOGzyMmwPs-nRyYL3QeV00brlaQWFN7pnjquYHtgJZgkVZlIe1Hh_8mBzTSLygc3-0Xw3FKf1X6p_jOyyN7Qi3Wf5GHvBdp_sYyuBtXMYVwhKQ56lYBX3waLP0KHSiDiDUW
```