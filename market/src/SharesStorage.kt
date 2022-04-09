package org.example

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class SharesStorage(val priceType: PriceType) {
    private val shares: MutableMap<String, Shares> = ConcurrentHashMap()

    private fun mapPrice(shares: Shares): Shares {
        return if (priceType == PriceType.MANUAL) {
            shares
        } else {
            shares.copy(
                price = shares.price + Random(LocalDateTime.now().hour).nextLong(
                    -shares.price / 2, shares.price
                )
            )
        }
    }

    fun getShares(): List<Shares> {
        return shares.values.toList().map(this::mapPrice)
    }

    fun getShares(companyName: String): Shares {
        return mapPrice(
            shares[companyName] ?: throw IllegalArgumentException("No shares registered for '$companyName'")
        )
    }

    fun addShares(companyName: String, price: Long) {
        check(price > 0) { "Shares can't cost non-positive amount" }
        check(companyName !in shares.keys) { "Company '$companyName' already registered" }
        shares[companyName] = Shares(
            companyName,
            0,
            price
        )
    }

    fun increaseShares(companyName: String, amount: Long): Long {
        check(amount > 0) { "Can't increase by non-positive amount" }
        val oldValue = shares[companyName]
            ?: throw IllegalArgumentException("Company '$companyName' is not registered")
        shares[companyName] = oldValue.copy(amount = oldValue.amount + amount)
        return oldValue.price * amount
    }

    fun decreaseShares(companyName: String, amount: Long): Long {
        check(amount > 0) { "Can't decreasy by non-positive amount" }
        val oldValue = shares[companyName]
            ?: throw IllegalArgumentException("Company '$companyName' is not registered")
        check(oldValue.amount >= amount) { "Can't decrease into negative amount" }
        shares[companyName] = oldValue.copy(amount = oldValue.amount - amount)
        return oldValue.price * amount
    }

    fun changePrice(companyName: String, newPrice: Long) {
        check(priceType == PriceType.MANUAL) { "Can't change price manually" }
        check(newPrice > 0) { "Shares can't const non-positive amount" }
        val oldValue = shares[companyName]
            ?: throw IllegalArgumentException("Company '$companyName' is not registered")
        shares[companyName] = oldValue.copy(price = newPrice)
    }
}
