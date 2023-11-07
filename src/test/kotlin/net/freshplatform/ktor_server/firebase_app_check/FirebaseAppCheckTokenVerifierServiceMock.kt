package net.freshplatform.ktor_server.firebase_app_check

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.delay
import net.freshplatform.ktor_server.firebase_app_check.exceptions.FirebaseAppCheckVerifyJwtErrorType
import net.freshplatform.ktor_server.firebase_app_check.exceptions.FirebaseAppCheckVerifyJwtException
import net.freshplatform.ktor_server.firebase_app_check.services.FetchFirebaseAppCheckPublicKeyConfig
import net.freshplatform.ktor_server.firebase_app_check.services.FirebaseAppCheckTokenVerifierService
import java.security.PublicKey
import kotlin.time.Duration.Companion.milliseconds

class PublicKeyMock : PublicKey {
    override fun getAlgorithm(): String {
        return "RS256"
    }

    override fun getFormat(): String {
        return "JWT"
    }

    override fun getEncoded(): ByteArray {
        return byteArrayOf()
    }
}

class FirebaseAppCheckTokenVerifierServiceMock : FirebaseAppCheckTokenVerifierService {
    override suspend fun fetchFirebaseAppCheckPublicKey(
        jwtString: String,
        url: String,
        config: FetchFirebaseAppCheckPublicKeyConfig
    ): PublicKey {
        delay(20.milliseconds)
        return PublicKeyMock()
    }

    override suspend fun verifyFirebaseAppCheckToken(
        jwtString: String,
        publicKey: PublicKey,
        firebaseProjectId: String,
        firebaseProjectNumber: String,
        issuerBaseUrl: String
    ): DecodedJWT {
        try {
            val verified = JWT.decode(jwtString)
            if (verified.audience.first() != "projects/$firebaseProjectNumber") {
                throw FirebaseAppCheckVerifyJwtException(
                    "The ${verified.audience.first()} is not equal to projects/$firebaseProjectNumber",
                    errorType = FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError
                )
            }
            if (verified.audience[1] != "projects/$firebaseProjectId") {
                throw FirebaseAppCheckVerifyJwtException(
                    "The ${verified.audience[1]} is not equal to projects/$firebaseProjectId",
                    errorType = FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError
                )
            }
            if (verified.issuer != "https://firebaseappcheck.googleapis.com/$firebaseProjectNumber") {
                throw FirebaseAppCheckVerifyJwtException(
                    "The ${verified.issuer} is not equal to projects/$firebaseProjectNumber",
                    errorType = FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError
                )
            }
            return verified
        } catch (e: JWTDecodeException) {
            throw FirebaseAppCheckVerifyJwtException(
                "Token is not valid: $e",
                errorType = FirebaseAppCheckVerifyJwtErrorType.TokenIsNotValid
            )
        }
    }
}