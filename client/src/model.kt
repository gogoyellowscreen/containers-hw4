package org.example

data class Shares(
    val companyName: String,
    val amount: Long,
    val price: Long? = null
)

data class User(
    val id: Long,
    val name: String,
    val balance: Long,
    val shares: List<Shares>
)
