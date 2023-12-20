package net.freshplatform.ktor_server.firebase_app_check.service

import net.freshplatform.ktor_server.firebase_app_check.service.jwt.DecodedJwt

actual class FirebaseAppCheckTokenVerifierServiceImpl : FirebaseAppCheckTokenVerifierService {
    override suspend fun verifyFirebaseAppCheckToken(
        firebaseAppCheckTokenJwt: String,
        firebaseProjectId: String,
        firebaseProjectNumber: String,
        issuerBaseUrl: String,
        publicKeyUrl: String
    ): DecodedJwt {
        TODO("Not yet implemented")
    }
}