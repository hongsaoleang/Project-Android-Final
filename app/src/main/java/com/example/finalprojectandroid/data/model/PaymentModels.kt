package com.example.finalprojectandroid.data.model

data class PaymentResponse(
    val id: Long,
    val orderId: Long,
    val qr: String,
    val status: String,
    val expiredAt: String
)

data class PaymentStatusResponse(
    val id: Long = 0,
    val orderId: Long = 0,
    val status: String,
    val qr: String? = null
)
