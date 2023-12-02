package net.freshplatform.ktor_server.firebase_app_check.core

import net.freshplatform.ktor_server.firebase_app_check.utils.extensions.protectRouteWithAppCheck

/**
 * A sealed class that defines different strategies for securing routes with Firebase App Check.
 * If you want to secure the whole app use [FirebaseAppCheckSecureStrategy.ProtectAll] for all the requests
 * if you want a specific routes in the app use [FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes]
 * * and then protect the routes in the routing by surround them with [protectRouteWithAppCheck]
 * if you want to protect routes by the path of the route as string use
 * [FirebaseAppCheckSecureStrategy.ProtectRoutesByPaths]
 */
sealed class FirebaseAppCheckSecureStrategy {
    data object ProtectAll : FirebaseAppCheckSecureStrategy()
    data class ProtectRoutesByPaths(
        val routesPaths: List<String>
    ) : FirebaseAppCheckSecureStrategy()

    data object ProtectSpecificRoutes : FirebaseAppCheckSecureStrategy()
}