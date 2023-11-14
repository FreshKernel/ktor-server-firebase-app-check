package net.freshplatform.ktor_server.firebase_app_check.services

import com.auth0.jwt.interfaces.DecodedJWT
import java.security.PublicKey
import java.util.concurrent.TimeUnit

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
 * @param timeUnit The time unit for cache expiration.
 */
data class FetchFirebaseAppCheckPublicKeyCacheConfig(
    val cacheSize: Long = 10,
    val expiresIn: Long = 24,
    val timeUnit: TimeUnit = TimeUnit.HOURS,
)

/**
 * Configuration data class for rate limiting of key fetch requests. This class specifies
 * the bucket size, refill rate, and time unit for rate limiting.
 *
 * @param bucketSize The bucket size for rate limiting.
 * @param refillRate The time to refill the rate limit.
 * @param timeUnit The time unit for rate limit refilling.
 */
data class FetchFirebaseAppCheckPublicKeyRateLimitedConfig(
    val bucketSize: Long = 10,
    val refillRate: Long = 1,
    val timeUnit: TimeUnit = TimeUnit.MINUTES
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
    ): DecodedJWT
}