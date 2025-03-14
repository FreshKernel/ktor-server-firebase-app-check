package net.freshplatform.ktor_server.firebase_app_check.utils

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.plugin
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.application
import io.ktor.server.routing.intercept
import net.freshplatform.ktor_server.firebase_app_check.FirebaseAppCheckPlugin
import net.freshplatform.ktor_server.firebase_app_check.configurations.FirebaseAppCheckSecureStrategy

/**
 * A suspended function that verifies an incoming Firebase App Check token for an [ApplicationCall].
 * This function performs comprehensive verification checks on the provided token to ensure its validity and security.
 *
 * It also handles exceptions by default and forwards them to the error builder, along with request information.
 */
suspend fun ApplicationCall.verifyAppTokenRequest() {
    val pluginConfig = application.plugin(FirebaseAppCheckPlugin).config
    val call = this
    val pluginMessages = pluginConfig.pluginMessagesBuilder(pluginConfig)
    val firebaseAppCheckToken = call.request.header(pluginConfig.firebaseAppCheckHeaderName)
    if (firebaseAppCheckToken == null) {
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = pluginMessages.appCheckIsNotDefinedResponse,
        )
        return
    }

    if (firebaseAppCheckToken.isBlank()) {
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = pluginMessages.appCheckIsEmptyResponse
        )
        return
    }

    try {

        val verifiedJwt = pluginConfig.serviceImpl.verifyFirebaseAppCheckToken(
            firebaseProjectId = pluginConfig.firebaseProjectId,
            firebaseProjectNumber = pluginConfig.firebaseProjectNumber,
            firebaseAppCheckTokenJwt = firebaseAppCheckToken,
            issuerBaseUrl = pluginConfig.firebaseAppCheckApiBaseUrl,
            publicKeyUrl = pluginConfig.firebaseAppCheckPublicKeyUrl
        )
        // Optional: Check that the token’s subject matches your app’s App ID.
        val isShouldContinue = pluginConfig.additionalSecurityCheck(verifiedJwt)
        if (!isShouldContinue) {
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = pluginMessages.appCheckConditionFalseResponse,
            )
            return
        }
        pluginConfig.afterSecurityCheck(verifiedJwt)
//            if (pluginConfig.consumeTheTokenAfterUsingIt) {
//                try {
//                    val response = httpClient.post(
//                        "${pluginConfig.firebaseAppCheckApiBaseUrl}/v1beta/projects/${pluginConfig.firebaseProjectId}" +
//                                ":verifyAppCheckToken",
//                    ) {
//                        contentType(ContentType.Application.Json)
//                        setBody(
//                            mapOf(
//                                "app_check_token" to jwt.token
//                            )
//                        )
//                    }
//                    call.respond(response.bodyAsText())
//                } catch (e: Exception) {
//                    call.respond(e.toString())
//                }
//            }
    } catch (e: Exception) {
        pluginConfig.errorBuilder(e, call, pluginConfig)
    }
}

/**
 * Configures a route to be protected by Firebase App Check using a specific secure strategy.
 *
 * By using [FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes], this function ensures that only the defined
 * route and its associated handlers are protected by Firebase App Check.
 *
 * Example:
 * ```
    get("/test") {
        call.respondText { "Tis get test doesn't use firebase app check!" }
    }
    protectRouteWithAppCheck {
        post("/test") {
            call.respondText { "Tis post test is protected!" }
        }
    }
 * ```
 *
 * @param build A lambda that specifies the route and its handlers to be protected.
 */
fun Route.protectRouteWithAppCheck(
    build: Route.() -> Unit
) {
    val configuration = application.plugin(FirebaseAppCheckPlugin).config

    val protectedRoute = createChild(ProtectedRouteSelector())
    val isShouldVerifyToken = configuration.isShouldVerifyToken(application.developmentMode)

    if (isShouldVerifyToken) {
        protectedRoute.intercept(ApplicationCallPipeline.Call) { _ ->
            call.verifyAppTokenRequest()
        }
    }
    protectedRoute.build()
}

class ProtectedRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }

    override fun toString(): String = "protected"
}