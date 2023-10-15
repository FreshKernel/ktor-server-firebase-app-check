package net.freshplatform.ktor_server.firebase_app_check

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

val firebaseAppCheckTokenVerifier: FirebaseAppCheckTokenVerifier by lazy {
    FirebaseAppCheckTokenVerifierImpl()
}

/**
 * A Ktor server plugin for configuring Firebase App Check easily and with simplicity.
 * It is not affiliated with Firebase or Google and may not be suitable for production use yet.
 * This plugin is designed to facilitate the setup of Firebase App Check within a Ktor application.
 * It requires the following configurations: `firebaseProjectNumber` and `firebaseProjectId`.
 *
 * @param config The configuration object that holds Firebase App Check settings.
 */
class FirebaseAppCheckPlugin(
    internal val config: FirebaseAppCheckPluginConfiguration
) {
    companion object Plugin :
        BaseApplicationPlugin<ApplicationCallPipeline, FirebaseAppCheckPluginConfigurationHolder, FirebaseAppCheckPlugin> {
        // ...
        override val key: AttributeKey<FirebaseAppCheckPlugin>
            get() = AttributeKey("FirebaseAppCheck")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: FirebaseAppCheckPluginConfigurationHolder.() -> Unit
        ): FirebaseAppCheckPlugin {
            val configuration = FirebaseAppCheckPluginConfigurationHolder()
                .apply(configure).configuration
            require(configuration.firebaseProjectNumber.isNotBlank()) {
                "The firebase project number should not be blank."
            }
            require(configuration.firebaseProjectId.isNotBlank()) {
                "The firebase project id should not be blank."
            }

            val isShouldVerifyToken = configuration.isShouldVerifyToken(pipeline.environment)
            val secureStrategy = configuration.secureStrategy
            if (isShouldVerifyToken && secureStrategy !is FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes) {
                pipeline.intercept(ApplicationCallPipeline.Call) {

                    when (secureStrategy) {
                        FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes -> {
                            return@intercept
                        }

                        FirebaseAppCheckSecureStrategy.ProtectAll -> {
                            // Do nothing
                        }

                        is FirebaseAppCheckSecureStrategy.ProtectRoutesByPaths -> {
                            val uri = call.request.uri
                            if (!secureStrategy.routesPaths.contains(uri)) {
                                return@intercept
                            }
                        }
                    }


                    call.verifyAppTokenRequest()
                }
            }
            return FirebaseAppCheckPlugin(configuration)
        }
    }
}

/**
 * A suspended function that verifies an incoming Firebase App Check token for an [ApplicationCall].
 * This function performs comprehensive verification checks on the provided token to ensure its validity and security.
 *
 * It also handles exceptions by default and forwards them to the error builder, along with request information.
 */
private suspend fun ApplicationCall.verifyAppTokenRequest() {
    val pluginConfig = application.plugin(FirebaseAppCheckPlugin).config
    val call = this
    val pluginMessages = pluginConfig.pluginMessages
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

        val publicKey = firebaseAppCheckTokenVerifier.fetchFirebaseAppCheckPublicKey(
            jwtString = firebaseAppCheckToken,
            url = pluginConfig.firebaseAppCheckPublicJwtSetUrl,
            config = FetchFirebaseAppCheckPublicKeyConfig()
        )
        val verifiedJwt = firebaseAppCheckTokenVerifier.verifyFirebaseAppCheckToken(
            firebaseProjectId = pluginConfig.firebaseProjectId,
            firebaseProjectNumber = pluginConfig.firebaseProjectNumber,
            jwtString = firebaseAppCheckToken,
            publicKey = publicKey,
            issuerBaseUrl = pluginConfig.firebaseAppCheckApiBaseUrl
        )
        // Optional: Check that the token’s subject matches your app’s App ID.
        val isShouldContinue = pluginConfig.additionalSecurityCheck(verifiedJwt)
        if (!isShouldContinue) {
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = pluginMessages.appCheckConditionFalseResponse
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
 * @param build A lambda that specifies the route and its handlers to be protected.
 */
fun Route.protectRouteWithAppCheck(
    build: Route.() -> Unit
) {
    val configuration = application.plugin(FirebaseAppCheckPlugin).config

    val protectedRoute = createChild(ProtectedRouteSelector())
//    var isRouteProtected = protectedRoute.attributes.getOrNull(isProtectedRouteKey)
//    if (isRouteProtected == null) {
//        protectedRoute.attributes.put(isProtectedRouteKey, true)
//        isRouteProtected = true
//    }
    val isShouldVerifyToken = configuration.isShouldVerifyToken(environment)

    if (isShouldVerifyToken) {
        protectedRoute.intercept(ApplicationCallPipeline.Call) { _ ->
//            if (!isRouteProtected) {
//                return@intercept
//            }
            call.verifyAppTokenRequest()
        }
    }
    protectedRoute.build()
}

class ProtectedRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }

    override fun toString(): String = "protected"
}

//class UnProtectedRouteSelector : RouteSelector() {
//    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
//        return RouteSelectorEvaluation.Transparent
//    }
//
//    override fun toString(): String = "unprotected"
//}

///**
// * The plugin secure strategy need to be configured with [FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes]
// * This will only unprotect one route and his handlers
// * */
//fun Route.unProtectRouteWithAppCheck(
//    build: Route.() -> Route,
//) {
//    application.plugin(FirebaseAppCheckPlugin).config
//
//    val unProtectedRoute = createChild(UnProtectedRouteSelector())
//    unProtectedRoute.attributes.put(
//        isProtectedRouteKey,
//        false
//    )
//    unProtectedRoute.build()
//}