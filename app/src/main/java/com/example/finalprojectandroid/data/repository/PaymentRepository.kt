package com.example.finalprojectandroid.data.repository

import com.example.finalprojectandroid.data.api.RetrofitClient

class PaymentRepository {
    private val api = RetrofitClient.paymentApi

    suspend fun createPayment(orderId: Int) = api.createKhqrPayment(orderId)
    suspend fun getStatus(paymentId: Int) = api.getPaymentStatus(paymentId)
    suspend fun checkBakong(paymentId: Int) = api.checkBakong(paymentId)
}
