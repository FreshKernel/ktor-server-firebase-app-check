package net.freshplatform.ktor_server.firebase_app_check

/**
 * A custom exception class for Firebase App Check-related errors.
 *
 * @param message The exception message describing the error.
 */
open class FirebaseAppCheckException(
    override val message: String
): Exception(message)

/**
 * Enum that defines possible error types when fetching a public key for Firebase App Check.
 */
enum class FirebaseAppCheckFetchPublicKeyErrorType {
    SigningKeyNotFound,
    NetworkError,
    RateLimitReached,
    JwkError,
    UnknownError
}

/**
 * An exception class for errors related to fetching a public key for Firebase App Check.
 *
 * @param message The exception message describing the error.
 * @param errorType The specific error type related to the exception.
 */
class FirebaseAppCheckFetchPublicKeyException(
    override val message: String,
    val errorType: FirebaseAppCheckFetchPublicKeyErrorType
): FirebaseAppCheckException(message)

/**
 * Enum that defines possible error types when verifying a JWT for Firebase App Check.
 */
enum class FirebaseAppCheckVerifyJwtErrorType {
    TokenExpired,
    GenericJwtVerificationError,
    TokenIsNotValid,
    HeaderTypeIsNotJwt,
    TokenAlgorithmIsNotCorrect,
    TokenSignatureVerificationInvalid,
    TokenMissingClaim,
    TokenIncorrectClaim,
    UnknownError,
}

/**
 * An exception class for errors related to verifying a JWT for Firebase App Check.
 *
 * @param message The exception message describing the error.
 * @param errorType The specific error type related to the exception.
 */
class FirebaseAppCheckVerifyJwtException(
    override val message: String,
    val errorType: FirebaseAppCheckVerifyJwtErrorType
): FirebaseAppCheckException(message)