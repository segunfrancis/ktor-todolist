package com.segunfrancis

import com.segunfrancis.authentication.JwtConfig
import com.segunfrancis.authentication.hash
import com.segunfrancis.data.DatabaseFactory
import com.segunfrancis.plugin.configureTodoRoutes
import com.segunfrancis.plugin.configureUserRoutes
import com.segunfrancis.repository.InMemoryTodoRepository
import com.segunfrancis.repository.InMemoryUserRepository
import com.segunfrancis.repository.ToDoRepository
import com.segunfrancis.repository.UserRepository
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.routing.Routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(CallLogging)

    DatabaseFactory.initDatabase()
    val todoRepository: ToDoRepository = InMemoryTodoRepository()
    val userRepository: UserRepository = InMemoryUserRepository()
    val config = JwtConfig(userRepository)
    val hashFunction = { s: String -> hash(s) }

    install(ContentNegotiation) {
        gson {
            setLenient()
            setPrettyPrinting()
        }
    }

    install(Authentication) {
        jwt {
            config.configureKtorFeature(this)
        }
    }

    install(Routing) {
        configureTodoRoutes(todoRepository)
        configureUserRoutes(userRepository, config, hashFunction)
    }
}
