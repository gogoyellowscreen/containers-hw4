package org.example

import java.util.concurrent.ConcurrentHashMap

class UserStorage {
    private val users: MutableMap<Long, User> = ConcurrentHashMap()

    fun getUser(id: Long): User {
        return users[id] ?: throw IllegalArgumentException("No users with id '$id'")
    }

    fun addUser(id: Long, name: String) {
        check(id !in users.keys) { "User with this id already exists" }
        users[id] = User(id, name, 0, listOf())
    }

    fun increaseBalance(id: Long, amount: Long) {
        check(amount > 0) { "Can't increase by non-positive amount" }
        val oldValue = users[id] ?: error("No user with id '$id'")
        users[id] = oldValue.copy(balance = oldValue.balance + amount)
    }

    fun decreaseBalance(id: Long, amount: Long) {
        check(amount > 0) { "Can't decrease by non-positive amount" }
        val oldValue = users[id] ?: error("No user with id '$id'")
        check(oldValue.balance >= amount) { "Can't decrease into negative amount" }
        users[id] = oldValue.copy(balance = oldValue.balance - amount)
    }

    fun addShares(id: Long, companyName: String, amount: Long) {
        check(amount > 0) { "Can't increase by non-positive amount" }
        val oldValue = users[id] ?: error("No user with id '$id'")

        var flag = false
        val newShares = oldValue.shares.map {
            if (it.companyName == companyName) {
                flag = true
                it.copy(amount = it.amount + amount)
            } else {
                it
            }
        }
        users[id] = oldValue.copy(shares = if (flag) newShares else newShares + Shares(companyName, amount))
    }

    fun removeShares(id: Long, companyName: String, amount: Long) {
        check(amount > 0) { "Can't decrease by non-positive amount" }
        val oldValue = users[id] ?: error("No user with id '$id'")

        val newShares = oldValue.shares.map {
            if (it.companyName == companyName) {
                if (it.amount < amount) {
                    throw IllegalArgumentException("Can't decrease into negative amount")
                } else {
                    it.copy(amount = it.amount - amount)
                }
            } else {
                it
            }
        }.filter { it.amount >= 0 }
        users[id] = oldValue.copy(shares = newShares)
    }
}