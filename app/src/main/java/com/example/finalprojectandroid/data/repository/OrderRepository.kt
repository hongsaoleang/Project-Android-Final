package com.example.finalprojectandroid.data.repository

import com.example.finalprojectandroid.data.api.RetrofitClient
import com.example.finalprojectandroid.data.model.CreateOrderRequest

class OrderRepository {
    private val api = RetrofitClient.orderApi

    suspend fun createOrder(request: CreateOrderRequest) = api.createOrder(request)
    suspend fun getMyOrders() = api.getMyOrders()
}
