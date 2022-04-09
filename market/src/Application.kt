package org.example

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val storage = SharesStorage(PriceType.MANUAL)

    routing {
        get("/") {
            try {
                val companyName = call.getParameter<String>("company-name")
                if (companyName == null) {
                    call.respond(storage.getShares())
                } else {
                    call.respond(storage.getShares(companyName))
                }
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/admin/register") {
            try {
                val companyName = call.requireParameter<String>("company-name")
                val price = call.requireParameter<Long>("price")
                val amount = call.getParameter<Long>("amount")

                storage.addShares(companyName, price)
                if (amount != null) {
                    storage.increaseShares(companyName, amount)
                }
                call.respond("SUCCESS")
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/sell") {
            try {
                val companyName = call.requireParameter<String>("company-name")
                val amount = call.requireParameter<Long>("amount")
                call.respond(storage.increaseShares(companyName, amount))
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/buy") {
            try {
                val companyName = call.requireParameter<String>("company-name")
                val amount = call.requireParameter<Long>("amount")
                call.respond(storage.decreaseShares(companyName, amount))
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/admin/change") {
            try {
                val companyName = call.requireParameter<String>("company-name")
                val newPrice = call.requireParameter<Long>("new-price")

                storage.changePrice(companyName, newPrice)
                call.respond("SUCCESS")
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
