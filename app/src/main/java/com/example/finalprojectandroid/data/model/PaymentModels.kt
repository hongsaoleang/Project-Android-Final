package com.example.finalprojectandroid.data.model

data class PaymentResponse(
    val id: Int,
    val orderId: Int,
    val qr: String,
    val status: String,
    val expiredAt: String
)

data class PaymentStatusResponse(
    val status: String
)
