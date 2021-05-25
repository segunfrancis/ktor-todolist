package com.segunfrancis.plugin

import com.segunfrancis.authentication.JwtConfig
import com.segunfrancis.entities.LoginBody
import com.segunfrancis.repository.UserRepository
import io.ktor.application.Application
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

fun Application.configureUserRoutes(
    userRepository: UserRepository,
    config: JwtConfig,
    hashFunction: (String) -> String
) {

    routing {

        get("/currentUser") {
            val user = call.authentication.principal as UserRepository.User
            call.respond(user)
        }

        post("/login") {
            val loginBody = call.receive<LoginBody>()
            val hash = hashFunction(loginBody.password)

            try {
                val user = userRepository.getUser(loginBody.username)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                    return@post
                }
                if (user.passwordHash == hash) {
                    call.respondText(config.generateToken(user))
                }
            } catch (t: Throwable) {
                application.log.error(t.localizedMessage)
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
            }
        }

        post("/register") {
            val registrationBody = call.receive<LoginBody>()
            val username = registrationBody.username
            val password = registrationBody.password
            val passwordHash = hashFunction(password)

            try {
                val newUser = userRepository.registerUser(
                    userId = System.currentTimeMillis(),
                    username = username,
                    passwordHash
                )
                if (newUser == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid credentials")
                    return@post
                }
                call.respondText(
                    config.generateToken(newUser),
                    status = HttpStatusCode.Created
                )
            } catch (t: Throwable) {
                application.log.error(t.localizedMessage)
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Problem occurred while creating user"
                )
            }
        }
    }
}
