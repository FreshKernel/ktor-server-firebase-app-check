package net.freshplatform.ktor_server.firebase_app_check.services.jwt

import com.auth0.jwt.interfaces.Claim
import java.time.DateTimeException
import java.time.Instant
import java.util.*

fun main() {

}

data class DecodedJwt(
    val token: String,
    val header: String,
    val payload: String,
    val signature: String
)

data class JwtPayload(
    val issuer: String,
    val subject: String,
    val audience: String,
    val expiresAt: Long,
    val notBefore: Long,
    val issuedAt: Long,
    val id: String,
    val getClaim: () -> Unit,
    val claims: Map<String?, Claim?>?,
)

interface PayloadBase {

    /**
     * Get a Claim given its name. If the Claim wasn't specified in the Payload, a 'null claim'
     * will be returned. All the methods of that claim will return `null`.
     *
     * @param name the name of the Claim to retrieve.
     * @return a non-null Claim.
     */
    fun getClaim(name: String?): Claim?

    /**
     * Get the Claims defined in the Token.
     *
     * @return a non-null Map containing the Claims defined in the Token.
     */
    val claims: Map<String?, Claim?>?
}

interface HeaderBase {

    /**
     * Getter for the Algorithm "alg" claim defined in the JWT's Header. If the claim is missing, it will return null.
     *
     * @return the Algorithm defined or null.
     */
    fun getAlgorithm(): String?

    /**
     * Getter for the Type "typ" claim defined in the JWT's Header. If the claim is missing, it will return null.
     *
     * @return the Type defined or null.
     */
    fun getType(): String?

    /**
     * Getter for the Content Type "cty" claim defined in the JWT's Header. If the claim is missing, it will return null.
     *
     * @return the Content Type defined or null.
     */
    fun getContentType(): String?

    /**
     * Get the value of the "kid" claim, or null if it's not available.
     *
     * @return the Key ID value or null.
     */
    fun getKeyId(): String?

    /**
     * Get a Private Claim given its name. If the Claim wasn't specified in the Header, a 'null claim' will be
     * returned. All the methods of that claim will return `null`.
     *
     * @param name the name of the Claim to retrieve.
     * @return a non-null Claim.
     */
    fun getHeaderClaim(name: String?): Claim?
}