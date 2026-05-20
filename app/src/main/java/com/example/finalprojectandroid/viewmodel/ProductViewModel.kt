package com.example.finalprojectandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectandroid.data.model.CreateProductRequest
import com.example.finalprojectandroid.data.model.Product
import com.example.finalprojectandroid.data.model.UpdateProductRequest
import com.example.finalprojectandroid.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _products.value = repository.getProducts()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createProduct(request: CreateProductRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createProduct(request)
                loadProducts()
                _error.value = null
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to create product"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProduct(id: Long, request: UpdateProductRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateProduct(id, request)
                loadProducts()
                _error.value = null
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to update product"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteProduct(id)
                loadProducts()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Unable to delete product"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
