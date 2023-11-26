package net.freshplatform.ktor_server.firebase_app_check

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import net.freshplatform.ktor_server.firebase_app_check.core.FirebaseAppCheckPluginConfiguration
import net.freshplatform.ktor_server.firebase_app_check.exceptions.FirebaseAppCheckVerifyJwtErrorType
import net.freshplatform.ktor_server.firebase_app_check.exceptions.FirebaseAppCheckVerifyJwtException
import net.freshplatform.ktor_server.firebase_app_check.service.FirebaseAppCheckTokenVerifierService
import net.freshplatform.ktor_server.firebase_app_check.utils.FirebaseAppCheckMessages
import net.freshplatform.ktor_server.firebase_app_check.utils.extensions.protectRouteWithAppCheck
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
            firebaseProjectId = TestConstants.FIREBASE_PROJECT_ID,
            serviceImpl = firebaseAppCheckTokenVerifierService,
            isShouldVerifyToken = true
        )
        val messages = FirebaseAppCheckMessages(
            pluginConfiguration = pluginConfiguration,
        )
        install(FirebaseAppCheckPlugin) {
            firebaseProjectId = pluginConfiguration.firebaseProjectId
            firebaseProjectNumber = pluginConfiguration.firebaseProjectNumber
            serviceImpl = pluginConfiguration.serviceImpl
            isShouldVerifyToken = pluginConfiguration.isShouldVerifyToken
        }
        routing {
            get("/") {
                call.respondText(
                    text = TestConstants.APP_CHECK_NOT_REQUIRED_MSG,
                )
            }
            protectRouteWithAppCheck {
                route("/products") {
                    get("/1") {
                        call.respondText(
                            text = TestConstants.APP_CHECK_REQUIRED_MSG,
                        )
                    }
                    get("/2") {
                        call.respondText(
                            text = TestConstants.APP_CHECK_REQUIRED_MSG,
                        )
                    }
                }
            }
            route("/products") {
                get("/3") {
                    call.respondText(
                        text = TestConstants.APP_CHECK_NOT_REQUIRED_MSG,
                    )
                }
            }
            get("/test") {
                call.respondText(
                    text = TestConstants.APP_CHECK_NOT_REQUIRED_MSG,
                )
            }
            protectRouteWithAppCheck {
                post("/test") {
                    call.respondText(
                        text = TestConstants.APP_CHECK_REQUIRED_MSG,
                    )
                }
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
        assertEquals(
            FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError,
            verifiedJwtWithDifferentProjectId.errorType
        )

        val verifiedJwtWithDifferentProjectNumber = assertFailsWith<FirebaseAppCheckVerifyJwtException> {
            firebaseAppCheckTokenVerifierService.verifyFirebaseAppCheckToken(
                jwtString = jwtString,
                publicKey = publicKey,
                firebaseProjectId = pluginConfiguration.firebaseProjectId,
                firebaseProjectNumber = "32132312123",
                issuerBaseUrl = pluginConfiguration.firebaseAppCheckApiBaseUrl
            )
        }
        assertEquals(
            FirebaseAppCheckVerifyJwtErrorType.GenericJwtVerificationError,
            verifiedJwtWithDifferentProjectNumber.errorType
        )

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

        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(TestConstants.APP_CHECK_NOT_REQUIRED_MSG, bodyAsText())
        }

        (1..2).forEach { productNumber ->
            client.get("/products/${productNumber}").apply {
                assertEquals(HttpStatusCode.Unauthorized, status)
                assertEquals(messages.appCheckIsNotDefinedResponse, bodyAsText())
            }
            client.get("/products/${productNumber}") {
                headers {
                    header(pluginConfiguration.firebaseAppCheckHeaderName, "Bearer ${TestConstants.TOKEN_OF_THE_PROJECT}")
                }
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, status)
                assertEquals(messages.tokenIsNotValidResponse, bodyAsText())
            }

            client.get("/products/${productNumber}") {
                headers {
                    header(pluginConfiguration.firebaseAppCheckHeaderName, TestConstants.TOKEN_OF_THE_PROJECT)
                }
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(TestConstants.APP_CHECK_REQUIRED_MSG, bodyAsText())
            }
        }
        client.get("/products/3").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(TestConstants.APP_CHECK_NOT_REQUIRED_MSG, bodyAsText())
        }

        client.get("/test").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(TestConstants.APP_CHECK_NOT_REQUIRED_MSG, bodyAsText())
        }

        client.post("/test").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals(messages.appCheckIsNotDefinedResponse, bodyAsText())
        }

        client.post("/test") {
            headers {
                header(pluginConfiguration.firebaseAppCheckHeaderName, "Bearer ${TestConstants.TOKEN_OF_THE_PROJECT}")
            }
        }.apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
            assertEquals(messages.tokenIsNotValidResponse, bodyAsText())
        }

        client.post("/test") {
            headers {
                header(pluginConfiguration.firebaseAppCheckHeaderName, TestConstants.TOKEN_OF_THE_PROJECT)
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(TestConstants.APP_CHECK_REQUIRED_MSG, bodyAsText())
        }
    }
}
