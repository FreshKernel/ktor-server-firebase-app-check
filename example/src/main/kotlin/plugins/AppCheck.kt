package plugins

import io.ktor.server.application.*
import net.freshplatform.ktor_server.firebase_app_check.FirebaseAppCheckPlugin
import net.freshplatform.ktor_server.firebase_app_check.core.FirebaseAppCheckSecureStrategy
import net.freshplatform.ktor_server.firebase_app_check.services.FirebaseAppCheckTokenVerifierServiceImpl
import net.freshplatform.ktor_server.firebase_app_check.utils.FirebaseAppCheckMessages

fun Application.configureAppCheck() {
    install(FirebaseAppCheckPlugin) {
        firebaseProjectNumber = System.getenv("FIREBASE_PROJECT_NUMBER")
            ?: throw MissingEnvironmentVariableException("FIREBASE_PROJECT_NUMBER")
        firebaseProjectId = System.getenv("FIREBASE_PROJECT_ID")
            ?: throw MissingEnvironmentVariableException("FIREBASE_PROJECT_ID")
        isShouldVerifyToken = true
        serviceImpl = FirebaseAppCheckTokenVerifierServiceImpl()
        secureStrategy = FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes
        pluginMessagesBuilder = { configuration ->
            // Example of override a response message
            FirebaseAppCheckMessages(
                configuration,
                appCheckIsNotDefinedResponse = mapOf(
                    "error" to "${configuration.firebaseAppCheckHeaderName} is required"
                ),
            )
        }
    }
}