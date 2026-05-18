package com.example.finalprojectandroid.data.repository

import com.example.finalprojectandroid.data.api.RetrofitClient

class PaymentRepository {
    private val api = RetrofitClient.paymentApi

    suspend fun createPayment(orderId: Long) = api.createKhqrPayment(orderId)
    suspend fun getStatus(paymentId: Long) = api.getPaymentStatus(paymentId)
    suspend fun checkBakong(paymentId: Long) = api.checkBakong(paymentId)
}
