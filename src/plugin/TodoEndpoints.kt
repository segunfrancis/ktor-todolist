package com.segunfrancis.plugin

import com.segunfrancis.entities.TodoDraft
import com.segunfrancis.repository.ToDoRepository
import com.segunfrancis.repository.UserRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.routing

fun Application.configureTodoRoutes(
    toDoRepository: ToDoRepository
) {

    routing {

        authenticate {
            get("/") {
                call.respondText("Hello TodoList")
            }

            get("/currentUser") {
                val user = call.authentication.principal as UserRepository.User
                call.respond(user)
            }

            get("/todos") {
                val todos = toDoRepository.getAllTodos()
                if (todos.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.OK, "Empty Todo list")
                    return@get
                }
                call.respond(toDoRepository.getAllTodos())
            }

            get("todos/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "ID parameter has to be a number"
                    )
                    return@get
                }
                val todo = toDoRepository.getTodo(id)
                if (todo == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        "found no Todo for the provided item #$id"
                    )
                } else {
                    call.respond(todo)
                }
            }

            post("/todos") {
                val todoDraft = call.receive<TodoDraft>()
                val todo = toDoRepository.addTodo(todoDraft)
                if (todo != null) {
                    call.respond(todo)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Couldn't add todo")
                }
            }

            put("/todos/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "id parameter has to be a number"
                    )
                    return@put
                }
                val todoDraft = call.receive<TodoDraft>()
                val updated = toDoRepository.updateTodo(id, todoDraft)
                if (updated) call.respond(HttpStatusCode.OK)
                else call.respond(
                    HttpStatusCode.NotFound,
                    "found no todo with the id #$id"
                )
            }

            delete("/todos/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "id parameter has to be a number"
                    )
                    return@delete
                }
                val removed = toDoRepository.removeTodo(id)
                if (removed) call.respond(HttpStatusCode.OK)
                else call.respond(
                    HttpStatusCode.NotFound,
                    "found no todo with the id #$id"
                )
            }
        }
    }
}
