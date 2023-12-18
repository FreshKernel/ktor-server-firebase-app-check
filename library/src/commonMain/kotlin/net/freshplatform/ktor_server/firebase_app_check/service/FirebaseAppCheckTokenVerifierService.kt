package net.freshplatform.ktor_server.firebase_app_check.service

import net.freshplatform.ktor_server.firebase_app_check.services.jwt.DecodedJwt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


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
    val enabled: Boolean
)

/**
 * Object containing functions for fetching and verifying Firebase App Check tokens.
 */
interface FirebaseAppCheckTokenVerifierService {
    /**
     * Suspended function to fetch a Firebase App Check a public key.
     *
     * @param jwtString to get the kid which is the Key ID.
     * @param url The URL for fetching the public key.
     * @param config Configuration for public key fetching.
     * @return The fetched public key.
     */
    suspend fun fetchFirebaseAppCheckPublicKey(
        jwtString: String, url: String,
        config: FetchFirebaseAppCheckPublicKeyConfig = FetchFirebaseAppCheckPublicKeyConfig()
    ): PublicKey

    /**
     * Suspended function to verify a Firebase App Check token.
     *
     * @param jwtString The JWT string to be verified.
     * @param publicKey The public key used for verification.
     * @param firebaseProjectId The Firebase project ID.
     * @param firebaseProjectNumber The Firebase project number.
     * @param issuerBaseUrl The base URL of the Firebase App Check issuer.
     * @return The verified Decoded JWT.
     */
    suspend fun verifyFirebaseAppCheckToken(
        jwtString: String,
        publicKey: PublicKey,
        firebaseProjectId: String,
        firebaseProjectNumber: String,
        issuerBaseUrl: String
    ): DecodedJwt
}