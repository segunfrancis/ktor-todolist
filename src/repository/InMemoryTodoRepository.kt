package com.segunfrancis.repository

import com.segunfrancis.data.DatabaseFactory.dbQuery
import com.segunfrancis.data.Todos
import com.segunfrancis.entities.Todo
import com.segunfrancis.entities.TodoDraft
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.update

class InMemoryTodoRepository : ToDoRepository {

    override suspend fun getAllTodos(): List<Todo?> {
        return dbQuery { Todos.selectAll().map { rowToTodo(it) } }
    }

    override suspend fun getTodo(id: Int): Todo? {
        return dbQuery {
            Todos.select { Todos.id.eq(id) }.map {
                rowToTodo(it)
            }.singleOrNull()
        }
    }

    override suspend fun addTodo(draft: TodoDraft): Todo? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Todos.insert { todo ->
                todo[title] = draft.title
                todo[done] = draft.done
            }
        }
        return rowToTodo(statement?.resultedValues?.get(0))
    }

    override suspend fun removeTodo(id: Int): Boolean {
        return dbQuery { Todos.deleteWhere { Todos.id eq id } } == 1
    }

    override suspend fun updateTodo(id: Int, draft: TodoDraft): Boolean {
        val response = dbQuery {
            Todos.update({ Todos.id eq id }) { todos ->
                todos[title] = draft.title
                todos[done] = draft.done
            }
        }
        return response == 1
    }

    private fun rowToTodo(row: ResultRow?): Todo? {
        if (row == null) {
            return null
        }
        return Todo(
            id = row[Todos.id],
            title = row[Todos.title],
            done = row[Todos.done]
        )
    }
}
