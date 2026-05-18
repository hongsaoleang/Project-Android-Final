package com.example.finalprojectandroid.data.api

import com.example.finalprojectandroid.data.model.PaymentResponse
import com.example.finalprojectandroid.data.model.PaymentStatusResponse
import retrofit2.http.*

interface PaymentApiService {
    @POST("api/payments/khqr/orders/{orderId}")
    suspend fun createKhqrPayment(@Path("orderId") orderId: Long): PaymentResponse

    @GET("api/payments/{paymentId}/status")
    suspend fun getPaymentStatus(@Path("paymentId") paymentId: Long): PaymentResponse

    @POST("api/payments/{paymentId}/check-bakong")
    suspend fun checkBakong(@Path("paymentId") paymentId: Long): PaymentResponse
}
