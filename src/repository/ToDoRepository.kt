package com.segunfrancis.repository

import com.segunfrancis.entities.Todo
import com.segunfrancis.entities.TodoDraft

interface ToDoRepository {

    suspend fun getAllTodos(): List<Todo?>

    suspend fun getTodo(id: Int): Todo?

    suspend fun addTodo(draft: TodoDraft): Todo?

    suspend fun removeTodo(id: Int): Boolean

    suspend fun updateTodo(id: Int, draft: TodoDraft): Boolean
}