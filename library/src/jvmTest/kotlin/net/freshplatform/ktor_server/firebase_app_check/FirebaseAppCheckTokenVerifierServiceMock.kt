package net.freshplatform.ktor_server.firebase_app_check

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import net.freshplatform.ktor_server.firebase_app_check.service.FirebaseAppCheckTokenVerifierService
import net.freshplatform.ktor_server.firebase_app_check.service.jwt.DecodedJwt
import java.security.PublicKey

private class PublicKeyMock : PublicKey {
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

    override suspend fun verifyFirebaseAppCheckToken(
        firebaseAppCheckTokenJwt: String,
        firebaseProjectId: String,
        firebaseProjectNumber: String,
        issuerBaseUrl: String,
        publicKeyUrl: String
    ): DecodedJwt {
        try {
            val verified = JWT.decode(firebaseAppCheckTokenJwt)
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
            return DecodedJwt(verified.token)
        } catch (e: JWTDecodeException) {
            throw FirebaseAppCheckVerifyJwtException(
                "Token is not valid: $e",
                errorType = FirebaseAppCheckVerifyJwtErrorType.TokenIsNotValid
            )
        }
    }
}