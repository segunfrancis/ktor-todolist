package com.segunfrancis.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.segunfrancis.repository.UserRepository
import io.ktor.auth.jwt.JWTAuthenticationProvider
import java.util.*

class JwtConfig(private val repository: UserRepository) {

    companion object Constants {
        // jwt config
        private const val jwtIssuer = "com.segunfrancis"
        private const val jwtRealm = "com.segunfrancis.todolist"

        // claims
        private const val CLAIM_USERID = "userId"
        private const val CLAIM_USERNAME = "userName"
    }

    private val jwtSecret = System.getenv("JWT_SECRET")
    private val jwtAlgorithm = Algorithm.HMAC512(jwtSecret)
    private val jwtVerifier: JWTVerifier = JWT
        .require(jwtAlgorithm)
        .withIssuer(jwtIssuer)
        .build()

    /**
     * Generate a token for a authenticated user
     */
    fun generateToken(user: UserRepository.User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(jwtIssuer)
        .withClaim(CLAIM_USERID, user.userId)
        .withClaim(CLAIM_USERNAME, user.username)
        .withExpiresAt(expiresAt())
        .sign(jwtAlgorithm)

    private fun expiresAt() =
        Date(System.currentTimeMillis() + 3_600_000 * 24) // 24 hours

    /**
     * Configure the jwt ktor authentication feature
     */
    fun configureKtorFeature(config: JWTAuthenticationProvider.Configuration) = with(config) {
        verifier(jwtVerifier)
        realm = jwtRealm
        validate {
            val userId = it.payload.getClaim(CLAIM_USERID).asInt()
            val userName = it.payload.getClaim(CLAIM_USERNAME).asString()

            if (userId != null && userName != null) {
                repository.getUser(userName)
            } else {
                null
            }
        }
    }
}
