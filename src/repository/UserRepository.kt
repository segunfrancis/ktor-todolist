package com.segunfrancis.repository

import io.ktor.auth.Principal

interface UserRepository {

    suspend fun getUser(username: String): User?

    suspend fun registerUser(userId: Long, username: String, passwordHash: String): User?

    data class User(
        val userId: Long,
        val username: String,
        val passwordHash: String
    ): Principal
}
