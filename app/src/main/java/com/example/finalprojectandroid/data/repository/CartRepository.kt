package com.example.finalprojectandroid.data.repository

import com.example.finalprojectandroid.data.model.CartItem
import com.example.finalprojectandroid.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    fun addToCart(product: Product) {
        _cartItems.update { currentItems ->
            val existing = currentItems.find { it.product.id == product.id }
            if (existing != null) {
                currentItems.map { if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it }
            } else {
                currentItems + CartItem(product, 1)
            }
        }
    }

    fun removeFromCart(productId: Long) {
        _cartItems.update { it.filter { item -> item.product.id != productId } }
    }

    fun updateQuantity(productId: Long, quantity: Int) {
        _cartItems.update { currentItems ->
            if (quantity <= 0) {
                currentItems.filter { it.product.id != productId }
            } else {
                currentItems.map { item ->
                    if (item.product.id == productId) item.copy(quantity = quantity) else item
                }
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
