package com.example.finalprojectandroid.data.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val stock: Int
)

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val stock: Int
)

data class UpdateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val stock: Int
)
