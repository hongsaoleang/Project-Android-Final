package com.example.finalprojectandroid.data.api

import com.example.finalprojectandroid.data.model.CreateOrderRequest
import com.example.finalprojectandroid.data.model.Order
import retrofit2.http.*

interface OrderApiService {
    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Order

    @GET("api/orders/my")
    suspend fun getMyOrders(): List<Order>
}
