package com.segunfrancis.data

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId: Column<Long> = long("userId")
    val username: Column<String> = varchar("username", 126)
    val passwordHash: Column<String> = varchar("password", 64)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userId)
}
