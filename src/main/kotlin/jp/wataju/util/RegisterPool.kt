package jp.wataju.util

object RegisterPool {
    lateinit var signing: Signing
    lateinit var product: Product
}

data class Signing(
    val identifier: String,
    val password: String,
    val administrator: Boolean
)

data class Product(
    val productName: String,
    val productNameKana: String,
    val price: Int,
    val enabled: Boolean
)
