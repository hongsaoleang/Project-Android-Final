package com.example.finalprojectandroid.data.repository

import com.example.finalprojectandroid.data.api.RetrofitClient
import com.example.finalprojectandroid.data.model.CreateProductRequest
import com.example.finalprojectandroid.data.model.Product
import com.example.finalprojectandroid.data.model.UpdateProductRequest

class ProductRepository {
    private val api = RetrofitClient.productApi

    suspend fun getProducts() = api.getProducts()
    suspend fun getProduct(id: Long) = api.getProductById(id)
    suspend fun createProduct(request: CreateProductRequest) = api.createProduct(request)
    suspend fun updateProduct(id: Long, request: UpdateProductRequest) = api.updateProduct(id, request)
    suspend fun deleteProduct(id: Long) = api.deleteProduct(id)
}
