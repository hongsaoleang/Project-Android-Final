package com.example.finalprojectandroid.data.api

import com.example.finalprojectandroid.data.model.CreateProductRequest
import com.example.finalprojectandroid.data.model.Product
import com.example.finalprojectandroid.data.model.UpdateProductRequest
import retrofit2.http.*

interface ProductApiService {
    @GET("api/products")
    suspend fun getProducts(): List<Product>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product

    @POST("api/products")
    suspend fun createProduct(@Body request: CreateProductRequest): Product

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body request: UpdateProductRequest): Product

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int)
}
