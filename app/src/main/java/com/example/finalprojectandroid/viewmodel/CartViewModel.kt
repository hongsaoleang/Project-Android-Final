package com.example.finalprojectandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectandroid.data.model.CreateOrderRequest
import com.example.finalprojectandroid.data.model.OrderItemRequest
import com.example.finalprojectandroid.data.repository.CartRepository
import com.example.finalprojectandroid.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CartViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {
    val cartItems = CartRepository.cartItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun checkout(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            _error.value = null
            _isLoading.value = true
            try {
                val currentItems = cartItems.value
                if (currentItems.isEmpty()) {
                    _error.value = "Your cart is empty"
                    return@launch
                }

                val items = currentItems.map { OrderItemRequest(it.product.id, it.quantity) }
                val order = orderRepository.createOrder(CreateOrderRequest(items))
                
                // Clear cart only after success
                CartRepository.clearCart()
                onSuccess(order.id)
            } catch (e: HttpException) {
                _error.value = "Server error: ${e.code()}. Check if your backend is running."
            } catch (e: IOException) {
                _error.value = "Network error. Make sure your local server (10.0.2.2) is online."
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromCart(productId: Long) {
        CartRepository.removeFromCart(productId)
    }

    fun increaseQuantity(productId: Long) {
        val item = cartItems.value.firstOrNull { it.product.id == productId } ?: return
        CartRepository.updateQuantity(productId, item.quantity + 1)
    }

    fun decreaseQuantity(productId: Long) {
        val item = cartItems.value.firstOrNull { it.product.id == productId } ?: return
        CartRepository.updateQuantity(productId, item.quantity - 1)
    }
}
