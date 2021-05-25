package com.segunfrancis.repository

import com.segunfrancis.data.DatabaseFactory.dbQuery
import com.segunfrancis.data.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class InMemoryUserRepository : UserRepository {

    override suspend fun getUser(username: String): UserRepository.User? {
        return dbQuery {
            Users.select {
                Users.username eq username
            }.map { rowToUser(it) }.firstOrNull()
        }
    }

    override suspend fun registerUser(userId: Long, username: String, passwordHash: String): UserRepository.User? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Users.insert { users ->
                users[Users.userId] = userId
                users[Users.username] = username
                users[this.passwordHash] = passwordHash
            }
        }
        return rowToUser(statement?.resultedValues?.firstOrNull())
    }

    private fun rowToUser(row: ResultRow?): UserRepository.User? {
        if (row == null) {
            return null
        }
        return UserRepository.User(
            userId = row[Users.userId],
            username = row[Users.username],
            passwordHash = row[Users.passwordHash]
        )
    }
}