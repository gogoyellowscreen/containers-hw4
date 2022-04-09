package org.example

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import org.example.dao.SharesDAO
import org.example.dao.impl.HttpSharesDAO

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val userStorage = UserStorage()
    val sharesDAO: SharesDAO = HttpSharesDAO(
        "http://localhost:49152",
        userStorage
    )

    routing {
        get("/register") {
            try {
                val id = call.requireParameter<Long>("user-id")
                val name = call.requireParameter<String>("name")
                userStorage.addUser(id, name)
                call.respond("SUCCESS")
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/profile") {
            try {
                val id = call.requireParameter<Long>("user-id")
                call.respond(userStorage.getUser(id))
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/increase") {
            try {
                val id = call.requireParameter<Long>("user-id")
                val amount = call.requireParameter<Long>("amount")
                userStorage.increaseBalance(id, amount)
                call.respond("SUCCESS")
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/totals") {
            try {
                val id = call.requireParameter<Long>("user-id")
                val newShares = sharesDAO.getTotalWorth(
                    userStorage.getUser(id).shares
                )
                call.respond(
                    mapOf(
                        "details" to newShares,
                        "total" to newShares.sumOf { it.amount * it.price!! }
                    )
                )
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/buy") {
            try {
                val id = call.requireParameter<Long>("user-id")
                val companyName = call.requireParameter<String>("company-name")
                val amount = call.requireParameter<Long>("amount")

                sharesDAO.buy(id, companyName, amount)
                call.respond("SUCCESS")
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/sell") {
            try {
                val id = call.requireParameter<Long>("user-id")
                val companyName = call.requireParameter<String>("company-name")
                val amount = call.requireParameter<Long>("amount")

                sharesDAO.sell(id, companyName, amount)
                call.respond("SUCCESS")
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

