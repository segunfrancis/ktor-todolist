package com.segunfrancis.data

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Todos : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val title: Column<String> = varchar("title", 512)
    val done: Column<Boolean> = bool("done")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}
