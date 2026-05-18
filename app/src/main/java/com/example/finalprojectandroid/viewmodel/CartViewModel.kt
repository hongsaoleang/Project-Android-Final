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

class CartViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {
    val cartItems = CartRepository.cartItems

    private val _orderId = MutableStateFlow<Int?>(null)
    val orderId = _orderId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun checkout(onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = cartItems.value.map { OrderItemRequest(it.product.id, it.quantity) }
                if (items.isEmpty()) {
                    _error.value = "Your cart is empty"
                    return@launch
                }
                val order = orderRepository.createOrder(CreateOrderRequest(items))
                _error.value = null
                CartRepository.clearCart()
                onSuccess(order.id)
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to create order"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromCart(productId: Int) {
        CartRepository.removeFromCart(productId)
    }

    fun increaseQuantity(productId: Int) {
        val item = cartItems.value.firstOrNull { it.product.id == productId } ?: return
        CartRepository.updateQuantity(productId, item.quantity + 1)
    }

    fun decreaseQuantity(productId: Int) {
        val item = cartItems.value.firstOrNull { it.product.id == productId } ?: return
        CartRepository.updateQuantity(productId, item.quantity - 1)
    }
}
