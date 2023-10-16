package net.freshplatform.ktor_server.firebase_app_check.core

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.*
import net.freshplatform.ktor_server.firebase_app_check.services.FirebaseAppCheckTokenVerifierService
import net.freshplatform.ktor_server.firebase_app_check.services.FirebaseAppCheckTokenVerifierServiceImpl
import net.freshplatform.ktor_server.firebase_app_check.utils.extensions.verifyAppTokenRequest

val firebaseAppCheckTokenVerifierService: FirebaseAppCheckTokenVerifierService by lazy {
    FirebaseAppCheckTokenVerifierServiceImpl()
}

/**
 * A configuration holder class for Firebase App Check plugin.
 * It is used to configure the Firebase App Check settings.
 */
class FirebaseAppCheckPluginConfigurationHolder {
    var configuration = FirebaseAppCheckPluginConfiguration()
}

/**
 * A Ktor server plugin for configuring Firebase App Check easily and with simplicity.
 * It is not affiliated with Firebase or Google and may not be suitable for production use yet.
 * This plugin is designed to facilitate the setup of Firebase App Check within a Ktor application.
 * It requires the following configurations: `firebaseProjectNumber` and `firebaseProjectId`.
 *
 * @param config The configuration object that holds Firebase App Check settings.
 */
class FirebaseAppCheckPlugin(
    internal val config: FirebaseAppCheckPluginConfiguration
) {
    companion object Plugin :
        BaseApplicationPlugin<ApplicationCallPipeline, FirebaseAppCheckPluginConfigurationHolder, FirebaseAppCheckPlugin> {
        // ...
        override val key: AttributeKey<FirebaseAppCheckPlugin>
            get() = AttributeKey("FirebaseAppCheck")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: FirebaseAppCheckPluginConfigurationHolder.() -> Unit
        ): FirebaseAppCheckPlugin {
            val configuration = FirebaseAppCheckPluginConfigurationHolder()
                .apply(configure).configuration
            require(configuration.firebaseProjectNumber.isNotBlank()) {
                "The firebase project number should not be blank."
            }
            require(configuration.firebaseProjectId.isNotBlank()) {
                "The firebase project id should not be blank."
            }

            val isShouldVerifyToken = configuration.isShouldVerifyToken(pipeline.environment)
            val secureStrategy = configuration.secureStrategy
            if (isShouldVerifyToken && secureStrategy !is FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes) {
                pipeline.intercept(ApplicationCallPipeline.Call) {

                    when (secureStrategy) {
                        FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes -> {
                            return@intercept
                        }

                        FirebaseAppCheckSecureStrategy.ProtectAll -> {
                            // Do nothing (don't return)
                        }

                        is FirebaseAppCheckSecureStrategy.ProtectRoutesByPaths -> {
                            val uri = call.request.uri
                            if (!secureStrategy.routesPaths.contains(uri)) {
                                return@intercept
                            }
                        }
                    }


                    call.verifyAppTokenRequest()
                }
            }
            return FirebaseAppCheckPlugin(configuration)
        }
    }
}