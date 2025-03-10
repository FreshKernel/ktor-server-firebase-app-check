package net.freshplatform.ktor_server.firebase_app_check

import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.request.uri
import io.ktor.util.AttributeKey
import net.freshplatform.ktor_server.firebase_app_check.configurations.FirebaseAppCheckPluginConfiguration
import net.freshplatform.ktor_server.firebase_app_check.configurations.FirebaseAppCheckSecureStrategy
import net.freshplatform.ktor_server.firebase_app_check.service.FirebaseAppCheckTokenVerifierServiceImpl
import net.freshplatform.ktor_server.firebase_app_check.utils.verifyAppTokenRequest

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
        BaseApplicationPlugin<ApplicationCallPipeline, FirebaseAppCheckPluginConfiguration, FirebaseAppCheckPlugin> {
        override val key: AttributeKey<FirebaseAppCheckPlugin>
            get() = AttributeKey("FirebaseAppCheck")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: FirebaseAppCheckPluginConfiguration.() -> Unit
        ): FirebaseAppCheckPlugin {
            val configuration = FirebaseAppCheckPluginConfiguration(
                serviceImpl = FirebaseAppCheckTokenVerifierServiceImpl()
            )
                .apply(configure)
            require(configuration.firebaseProjectNumber.isNotBlank()) {
                "The firebase project number should not be blank."
            }
            require(configuration.firebaseProjectId.isNotBlank()) {
                "The firebase project id should not be blank."
            }

            val isShouldVerifyToken = configuration.isShouldVerifyToken(pipeline.developmentMode)
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