package com.example.finalprojectandroid.data.model

data class OrderItemRequest(
    val productId: Int,
    val quantity: Int
)

data class CreateOrderRequest(
    val items: List<OrderItemRequest>
)

data class OrderItem(
    val id: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double
)

data class Order(
    val id: Int,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val status: String,
    val createdAt: String
)
