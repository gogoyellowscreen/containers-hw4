package org.example

data class Shares(
    val companyName: String,
    val amount: Long,
    val price: Long
)

enum class PriceType {
    RANDOM, MANUAL
}
