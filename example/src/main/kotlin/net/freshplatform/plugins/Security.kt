package net.freshplatform.plugins

import io.ktor.server.application.*

fun Application.configureSecurity() {
    configureAppCheck()
}