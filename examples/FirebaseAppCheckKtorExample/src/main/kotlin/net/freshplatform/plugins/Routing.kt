package net.freshplatform.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.freshplatform.ktor_server.firebase_app_check.*

class MissingEnvironmentVariableException(variableName: String) :
    RuntimeException("The required environment variable '$variableName' is missing.")

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    install(FirebaseAppCheckPlugin) {
        configuration = FirebaseAppCheckPluginConfiguration(
            firebaseProjectNumber = System.getenv("FIREBASE_PROJECT_NUMBER")
                ?: throw MissingEnvironmentVariableException("FIREBASE_PROJECT_NUMBER"),
            firebaseProjectId = System.getenv("FIREBASE_PROJECT_ID")
                ?: throw MissingEnvironmentVariableException("FIREBASE_PROJECT_ID"),
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
}
