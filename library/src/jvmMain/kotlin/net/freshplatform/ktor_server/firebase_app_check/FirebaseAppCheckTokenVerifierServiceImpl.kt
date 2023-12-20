package net.freshplatform.ktor_server.firebase_app_check

import com.auth0.jwk.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.freshplatform.ktor_server.firebase_app_check.service.FetchFirebaseAppCheckPublicKeyConfig
import net.freshplatform.ktor_server.firebase_app_check.service.FirebaseAppCheckTokenVerifierService
import net.freshplatform.ktor_server.firebase_app_check.service.jwt.DecodedJwt
import java.net.URL
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import kotlin.time.toJavaDuration

class FirebaseAppCheckTokenVerifierServiceImpl : FirebaseAppCheckTokenVerifierService {
    override suspend fun verifyFirebaseAppCheckToken(
        firebaseAppCheckTokenJwt: String,
        firebaseProjectId: String,
        firebaseProjectNumber: String,
        issuerBaseUrl: String,
        publicKeyUrl: String
    ): DecodedJwt {
        val publicKey = fetchFirebaseAppCheckPublicKey(
            jwtString = firebaseAppCheckTokenJwt,
            url = publicKeyUrl,
            config = FetchFirebaseAppCheckPublicKeyConfig()
        )
        val verifiedJwt = verifyFirebaseAppCheckToken(
            firebaseProjectId = firebaseProjectId,
            firebaseProjectNumber = firebaseProjectNumber,
            jwtString = firebaseAppCheckTokenJwt,
            publicKey = publicKey,
            issuerBaseUrl = issuerBaseUrl
        )
        return verifiedJwt
    }
    private suspend fun fetchFirebaseAppCheckPublicKey(
        jwtString: String,
        url: String,
        config: FetchFirebaseAppCheckPublicKeyConfig
    ): PublicKey {
        return withContext(Dispatchers.IO) {
            try {
                val cacheConfig = config.cacheConfiguration
                val rateLimitedConfig = config.rateLimitedConfig
                val jwkProvider =
                    JwkProviderBuilder(URL(url))
                        .cached(
                            cacheConfig.cacheSize,
                            cacheConfig.expiresIn.toJavaDuration(),
                        )
                        .rateLimited(
                            rateLimitedConfig.enabled
                        )
                        .build()

                val decodedJwt = JWT.decode(jwtString)
                val kid = decodedJwt.getHeaderClaim("kid").asString()
                jwkProvider.get(kid).publicKey
            } catch (e: SigningKeyNotFoundException) {
                throw FirebaseAppCheckFetchPublicKeyException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckFetchPublicKeyErrorType.SigningKeyNotFound
                )
            } catch (e: NetworkException) {
                throw FirebaseAppCheckFetchPublicKeyException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckFetchPublicKeyErrorType.NetworkError
                )
            } catch (e: RateLimitReachedException) {
                throw FirebaseAppCheckFetchPublicKeyException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckFetchPublicKeyErrorType.RateLimitReached
                )
            } catch (e: JwkException) {
                throw FirebaseAppCheckFetchPublicKeyException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckFetchPublicKeyErrorType.JwkError
                )
            } catch (e: JWTDecodeException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenIsNotValid
                )
            } catch (e: AlgorithmMismatchException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenAlgorithmIsNotCorrect
                )
            } catch (e: SignatureVerificationException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenSignatureVerificationInvalid
                )
            } catch (e: MissingClaimException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenMissingClaim
                )
            } catch (e: IncorrectClaimException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenIncorrectClaim
                )
            } catch (e: JWTVerificationException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError
                )
            } catch (e: Exception) {
                throw FirebaseAppCheckFetchPublicKeyException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckFetchPublicKeyErrorType.UnknownError
                )
            }
        }
    }

    private suspend fun verifyFirebaseAppCheckToken(
        jwtString: String,
        publicKey: PublicKey,
        firebaseProjectId: String,
        firebaseProjectNumber: String,
        issuerBaseUrl: String
    ): DecodedJwt {
        return withContext(Dispatchers.IO) {
            try {
                val verifier = JWT
                    .require(Algorithm.RSA256(publicKey as RSAPublicKey, null))
                    .withAnyOfAudience("projects/${firebaseProjectNumber}", "projects/${firebaseProjectId}")
                    .withIssuer("${issuerBaseUrl}/${firebaseProjectNumber}")
                    .build()
                val decodedJwt = verifier.verify(jwtString)
                val tokenHeader = decodedJwt.getHeaderClaim("typ").asString()
                if (tokenHeader != "JWT") {
                    throw FirebaseAppCheckVerifyJwtException(
                        message = "The token header of value $tokenHeader is not equal to 'JWT'",
                        errorType = FirebaseAppCheckVerifyJwtErrorType.HeaderTypeIsNotJwt
                    )
                }
                DecodedJwt(decodedJwt.token)
            } catch (e: TokenExpiredException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenExpired
                )
            }  catch (e: JWTDecodeException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenIsNotValid
                )
            } catch (e: AlgorithmMismatchException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenAlgorithmIsNotCorrect
                )
            } catch (e: SignatureVerificationException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenSignatureVerificationInvalid
                )
            } catch (e: MissingClaimException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenMissingClaim
                )
            } catch (e: IncorrectClaimException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.TokenIncorrectClaim
                )
            } catch (e: JWTVerificationException) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError
                )
            } catch (e: Exception) {
                throw FirebaseAppCheckVerifyJwtException(
                    message = e.message.toString(),
                    errorType = FirebaseAppCheckVerifyJwtErrorType.UnknownError
                )
            }
        }
    }

}