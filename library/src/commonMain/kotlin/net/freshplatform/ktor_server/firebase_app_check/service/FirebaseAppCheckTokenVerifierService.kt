package net.freshplatform.ktor_server.firebase_app_check.service

import net.freshplatform.ktor_server.firebase_app_check.service.jwt.DecodedJwt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours


/**
 * Configuration data class for fetching Firebase App Check public keys. This class
 * encapsulates various configurations, including cache and rate limiting settings.
 *
 * @param cacheConfiguration Configuration for the cache of public keys.
 * @param rateLimitedConfig Configuration for rate limiting of key fetch requests.
 */
data class FetchFirebaseAppCheckPublicKeyConfig(
    val cacheConfiguration: FetchFirebaseAppCheckPublicKeyCacheConfig = FetchFirebaseAppCheckPublicKeyCacheConfig(),
    val rateLimitedConfig: FetchFirebaseAppCheckPublicKeyRateLimitedConfig = FetchFirebaseAppCheckPublicKeyRateLimitedConfig(),
)

/**
 * Configuration data class for the cache of public keys. This class defines the size
 * of the cache and the duration for keys to expire within the cache.
 *
 * @param cacheSize The size of the cache for public keys.
 * @param expiresIn The duration for public keys to expire in the cache.
 */
data class FetchFirebaseAppCheckPublicKeyCacheConfig(
    val cacheSize: Long = 10,
    val expiresIn: Duration = 24.hours,
)

/**
 * Configuration data class for rate limiting of key fetch requests.
 *
 * @param enabled
 */
data class FetchFirebaseAppCheckPublicKeyRateLimitedConfig(
    val enabled: Boolean = true
)

/**
 * Object containing functions for fetching and verifying Firebase App Check tokens.
 */
interface FirebaseAppCheckTokenVerifierService {

    /**
     * Suspended function to verify a Firebase App Check token.
     *
     * @param firebaseAppCheckTokenJwt The JWT string to be verified.
     * @param firebaseProjectId The Firebase project ID.
     * @param firebaseProjectNumber The Firebase project number.
     * @param issuerBaseUrl The base URL of the Firebase App Check issuer.
     * @return The verified Decoded JWT.
     */
    suspend fun verifyFirebaseAppCheckToken(
        firebaseAppCheckTokenJwt: String,
        firebaseProjectId: String,
        firebaseProjectNumber: String,
        issuerBaseUrl: String,
        publicKeyUrl: String
    ): DecodedJwt
}

expect class FirebaseAppCheckTokenVerifierServiceImpl(): FirebaseAppCheckTokenVerifierService