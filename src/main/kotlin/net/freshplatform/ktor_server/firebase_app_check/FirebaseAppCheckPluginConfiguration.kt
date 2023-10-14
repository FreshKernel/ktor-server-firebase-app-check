package net.freshplatform.ktor_server.firebase_app_check

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import net.freshplatform.ktor_server.firebase_app_check.FirebaseAppCheckFetchPublicKeyErrorType.*
import net.freshplatform.ktor_server.firebase_app_check.FirebaseAppCheckVerifyJwtErrorType.*

/**
 * A sealed class that defines different strategies for securing routes with Firebase App Check.
 */
sealed class FirebaseAppCheckSecureStrategy {
    data object ProtectAll : FirebaseAppCheckSecureStrategy()
    data class ProtectRoutesByPaths(
        val routesPaths: List<String>
    ) : FirebaseAppCheckSecureStrategy()

    data object ProtectSpecificRoutes : FirebaseAppCheckSecureStrategy()
}

/**
 * A configuration holder class for Firebase App Check plugin.
 * It is used to configure the Firebase App Check settings.
 */
class FirebaseAppCheckPluginConfigurationHolder {
    var configuration = FirebaseAppCheckPluginConfiguration()
}

/**
 * Configuration class for Firebase App Check plugin.
 * It defines the settings required to configure Firebase App Check for a Ktor application.
 * By default, the firebase app check run only in production mode
 * @property firebaseProjectId The Firebase project ID (required).
 * @property firebaseProjectNumber The Firebase project number (required).
 * @property overrideIsShouldVerifyToken If set, overrides the default behavior of when to verify tokens.
 * * if you want to override this behavior, please pass a value to [overrideIsShouldVerifyToken]
 * @property firebaseAppCheckHeaderName The name of the header used to pass the Firebase App Check token.
 * @property firebaseAppCheckApiBaseUrl The base URL for Firebase App Check API.
 * @property firebaseAppCheckPublicJwtSetUrl The URL for fetching the public JWT set.
 * @property secureStrategy The strategy used to secure specific routes.
 * @property additionalSecurityCheck A function to perform additional security checks on the decoded JWT.
 * @property afterSecurityCheck A function to execute after performing security checks.
 * @property errorBuilder A function to build responses for different error scenarios.
 */
class FirebaseAppCheckPluginConfiguration(
    var firebaseProjectId: String = "",
    var firebaseProjectNumber: String = "",
    var overrideIsShouldVerifyToken: Boolean? = null,
    var firebaseAppCheckHeaderName: String = "X-Firebase-AppCheck",
    var firebaseAppCheckApiBaseUrl: String = "https://firebaseappcheck.googleapis.com",
    var firebaseAppCheckPublicJwtSetUrl: String = "${firebaseAppCheckApiBaseUrl}/v1/jwks",
    var secureStrategy: FirebaseAppCheckSecureStrategy = FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes,
    var additionalSecurityCheck: suspend (decodedJwt: DecodedJWT) -> Boolean = {
        true
    },
    var afterSecurityCheck: suspend (decodedJwt: DecodedJWT) -> Unit = {},
    //    var consumeTheTokenAfterUsingIt: Boolean = false
    var errorBuilder: suspend (e: Exception, call: ApplicationCall, pluginConfig: FirebaseAppCheckPluginConfiguration) -> Unit
    = { e, call, pluginConfig ->
        val pluginMessages = pluginConfig.pluginMessages
        when (e) {
            is FirebaseAppCheckVerifyJwtException -> {
                when (e.errorType) {
                    TokenExpired -> {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            message = pluginMessages.tokenExpiredResponse
                        )
                    }

                    GenericJwtVerificationError -> {
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = pluginMessages.genericJwtVerificationErrorResponse
                        )
                    }

                    TokenIsNotValid -> {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            message = pluginMessages.tokenIsNotValidResponse
                        )
                    }

                    HeaderTypeIsNotJwt -> {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            message = pluginMessages.headerTypeIsNotJwtResponse
                        )
                    }

                    TokenAlgorithmIsNotCorrect -> {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            message = pluginMessages.tokenAlgorithmIsNotCorrectResponse
                        )
                    }

                    TokenSignatureVerificationInvalid -> {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            message = pluginMessages.tokenSignatureVerificationInvalidResponse
                        )
                    }

                    TokenMissingClaim -> {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            message = pluginMessages.tokenMissingClaimResponse
                        )
                    }

                    TokenIncorrectClaim -> {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            message = pluginMessages.tokenIncorrectClaimResponse
                        )
                    }

                    FirebaseAppCheckVerifyJwtErrorType.UnknownError -> {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            message = pluginMessages.verifyJwtUnhandledExceptionResponse
                        )
                    }
                }
            }
            is FirebaseAppCheckFetchPublicKeyException -> {
                when (e.errorType) {
                    SigningKeyNotFound -> {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            message = pluginMessages.signingKeyNotFoundResponse
                        )
                    }

                    NetworkError -> {
                        call.respond(
                            status = HttpStatusCode.BadGateway,
                            message = pluginMessages.networkErrorResponse
                        )
                    }

                    RateLimitReached -> {
                        call.respond(
                            status = HttpStatusCode.TooManyRequests,
                            message = pluginMessages.rateLimitReachedResponse
                        )
                    }
                    JwkError -> {
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = pluginMessages.jwkErrorResponse
                        )
                    }
                    FirebaseAppCheckFetchPublicKeyErrorType.UnknownError -> {
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = pluginMessages.fetchPublicKeyUnknownErrorResponse
                        )
                    }
                }
            }
            // You can also handle it using StatusPage ktor server plugin.
            // but we shouldn't handle all the exceptions in that case
            else -> {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = pluginMessages.unknownErrorResponse
                )
            }
        }
    },
) {

    var pluginMessages: FirebaseAppCheckMessages = FirebaseAppCheckMessages(
        pluginConfiguration = this,
    )

    /**
     * Determines whether token verification should be performed based on the environment (developmentMode).
     *
     * @param environment The application environment.
     * @return `true` if token verification should be performed; otherwise, `false`.
     */
    fun isShouldVerifyToken(environment: ApplicationEnvironment?): Boolean {
        overrideIsShouldVerifyToken?.let {
            return it
        }
        val isDevMode = environment?.developmentMode ?: false
        return !isDevMode
    }
}