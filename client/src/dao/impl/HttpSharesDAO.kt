package org.example.dao.impl

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.example.Shares
import org.example.UserStorage
import org.example.dao.SharesDAO

class HttpSharesDAO(
    private val baseURL: String,
    private val userStorage: UserStorage
) : SharesDAO {

    private val client: HttpClient = HttpClient(Apache) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
        expectSuccess = false
    }

    override suspend fun buy(userId: Long, companyName: String, amount: Long) {
        val shares = client.get<Shares>("$baseURL?company-name=$companyName")
        try {
            userStorage.decreaseBalance(userId, shares.price!! * amount)
            client.get<HttpResponse>("$baseURL/buy?company-name=$companyName&amount=$amount")
            userStorage.addShares(userId, companyName, amount)
        } catch (_: IllegalStateException) {
        }
    }

    override suspend fun sell(userId: Long, companyName: String, amount: Long) {
        val shares = client.get<Shares>("$baseURL?company-name=$companyName")
        try {
            userStorage.removeShares(userId, companyName, amount)
            client.get<HttpResponse>("$baseURL/sell?company-name=$companyName&amount=$amount")
            userStorage.increaseBalance(userId, shares.price!! * amount)
        } catch (e: IllegalStateException) {
        }
    }

    override suspend fun getTotalWorth(shares: List<Shares>): List<Shares> {
        return shares.map {
            client.get("$baseURL?company-name=${it.companyName}")
        }
    }
}
