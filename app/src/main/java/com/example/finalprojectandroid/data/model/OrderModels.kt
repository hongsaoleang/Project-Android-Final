package com.example.finalprojectandroid.data.model

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)

data class CreateOrderRequest(
    val items: List<OrderItemRequest>
)

data class OrderItem(
    val id: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Double
)

data class Order(
    val id: Long,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val status: String,
    val createdAt: String
)
