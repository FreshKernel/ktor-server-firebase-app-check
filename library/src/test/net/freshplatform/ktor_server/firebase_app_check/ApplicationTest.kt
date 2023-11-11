package net.freshplatform.ktor_server.firebase_app_check

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import net.freshplatform.ktor_server.firebase_app_check.core.FirebaseAppCheckPlugin
import net.freshplatform.ktor_server.firebase_app_check.core.FirebaseAppCheckPluginConfiguration
import net.freshplatform.ktor_server.firebase_app_check.exceptions.FirebaseAppCheckVerifyJwtErrorType
import net.freshplatform.ktor_server.firebase_app_check.exceptions.FirebaseAppCheckVerifyJwtException
import net.freshplatform.ktor_server.firebase_app_check.services.FirebaseAppCheckTokenVerifierService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

val firebaseAppCheckTokenVerifierService: FirebaseAppCheckTokenVerifierService by lazy {
    FirebaseAppCheckTokenVerifierServiceMock()
}

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val pluginConfiguration = FirebaseAppCheckPluginConfiguration(
            firebaseProjectNumber = TestConstants.FIREBASE_PROJECT_NUMBER,
            firebaseProjectId = TestConstants.FIREBASE_PROJECT_ID
        )
        application {
            install(Routing)
            install(FirebaseAppCheckPlugin) {
                configuration = pluginConfiguration
            }
        }
        val jwtString = TestConstants.TOKEN_OF_THE_PROJECT
        val publicKey = firebaseAppCheckTokenVerifierService.fetchFirebaseAppCheckPublicKey(
            jwtString = jwtString,
            url = pluginConfiguration.firebaseAppCheckPublicJwtSetUrl
        )
        try {
            firebaseAppCheckTokenVerifierService.verifyFirebaseAppCheckToken(
                jwtString = jwtString,
                publicKey = publicKey,
                firebaseProjectId = pluginConfiguration.firebaseProjectId,
                firebaseProjectNumber = pluginConfiguration.firebaseProjectNumber,
                issuerBaseUrl = pluginConfiguration.firebaseAppCheckApiBaseUrl
            )
        } catch (e: Exception) {
            fail("Test failed while verify the firebase app check token: $e")
        }


        val verifiedJwtWithDifferentProjectId = assertFailsWith<FirebaseAppCheckVerifyJwtException> {
            firebaseAppCheckTokenVerifierService.verifyFirebaseAppCheckToken(
                jwtString = jwtString,
                publicKey = publicKey,
                firebaseProjectId = "myapp-eb212",
                firebaseProjectNumber = pluginConfiguration.firebaseProjectNumber,
                issuerBaseUrl = pluginConfiguration.firebaseAppCheckApiBaseUrl
            )
        }
        assertEquals(FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError, verifiedJwtWithDifferentProjectId.errorType)

        val verifiedJwtWithDifferentProjectNumber = assertFailsWith<FirebaseAppCheckVerifyJwtException> {
            firebaseAppCheckTokenVerifierService.verifyFirebaseAppCheckToken(
                jwtString = jwtString,
                publicKey = publicKey,
                firebaseProjectId = pluginConfiguration.firebaseProjectId,
                firebaseProjectNumber = "32132312123",
                issuerBaseUrl = pluginConfiguration.firebaseAppCheckApiBaseUrl
            )
        }
        assertEquals(FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError, verifiedJwtWithDifferentProjectNumber.errorType)

        val invalidJwtException = assertFailsWith<FirebaseAppCheckVerifyJwtException> {
            firebaseAppCheckTokenVerifierService.verifyFirebaseAppCheckToken(
                jwtString = "eyInvalidJwt",
                publicKey = publicKey,
                firebaseProjectId = pluginConfiguration.firebaseProjectId,
                firebaseProjectNumber = pluginConfiguration.firebaseProjectNumber,
                issuerBaseUrl = pluginConfiguration.firebaseAppCheckApiBaseUrl
            )
        }

        assertEquals(FirebaseAppCheckVerifyJwtErrorType.TokenIsNotValid, invalidJwtException.errorType)

//        client.get("/").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Hello World!", bodyAsText())
//        }
    }
}
