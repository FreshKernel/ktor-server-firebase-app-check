package net.freshplatform.ktor_server.firebase_app_check.utils

import net.freshplatform.ktor_server.firebase_app_check.core.FirebaseAppCheckPluginConfiguration

/**
 * Data class containing messages related to Firebase App Check.
 * Messages include responses for scenarios like missing or empty App Check tokens.
 */
data class FirebaseAppCheckMessages(
    val pluginConfiguration: FirebaseAppCheckPluginConfiguration,
    val appCheckIsNotDefinedResponse: Any = "App check should be defined in the header: ${pluginConfiguration.firebaseAppCheckHeaderName}.",
    val appCheckIsEmptyResponse: Any = "App check in the header('${pluginConfiguration.firebaseAppCheckHeaderName}') should not be empty.",
    val appCheckConditionFalseResponse: Any = "App check token is not verified.",
    val tokenExpiredResponse: Any = "This firebase app check token is expired.",
    val genericJwtVerificationErrorResponse: Any = "Unknown error while verifying the firebase app check token. It's when we're trying to verify the jwt.",
    val tokenIsNotValidResponse: Any = "The firebase app check token is invalid.",
    val headerTypeIsNotJwtResponse: Any = "The type of this token in the header is not equal to 'jwt'.",
    val tokenAlgorithmIsNotCorrectResponse: Any = "The type of this algorithm in the token is not RSA256.",
    val tokenSignatureVerificationInvalidResponse: Any = "The token signature is invalid.",
    val tokenMissingClaimResponse: Any = "There are missing claims, make sure the kid exists and the token is valid.",
    val tokenIncorrectClaimResponse: Any = "There are incorrect claims, make sure the kid exists and the token is valid.",
    val verifyJwtUnhandledExceptionResponse: Any = "Unhandled exception while verifying the token.",
    val signingKeyNotFoundResponse: Any = "Can't find the signing key from Firebase App Check API.",
    val networkErrorResponse: Any = "There was a network error while fetching the public key from Firebase App Check.",
    val rateLimitReachedResponse: Any = "The limit has been reached. The Firebase App Check API no longer takes requests from us for now.",
    val jwkErrorResponse: Any = "Unknown error while getting the public key from Firebase App Check API. It's related to JWK.",
    val fetchPublicKeyUnknownErrorResponse: Any = "Unknown error while getting the public key from Firebase App Check API. It's related to JWK.",
    val unknownErrorResponse: Any = "Unknown while run the firebase app check feature.",
)
