package com.example.finalprojectandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectandroid.data.model.PaymentResponse
import com.example.finalprojectandroid.data.repository.PaymentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(private val repository: PaymentRepository = PaymentRepository()) : ViewModel() {
    private val _payment = MutableStateFlow<PaymentResponse?>(null)
    val payment = _payment.asStateFlow()

    private val _status = MutableStateFlow("PENDING")
    val status = _status.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var pollingJob: Job? = null

    fun startPayment(orderId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            pollingJob?.cancel()
            try {
                val response = repository.createPayment(orderId)
                _payment.value = response
                _status.value = response.status
                _error.value = null
                startPolling(response.id)
            } catch (e: Exception) {
                _status.value = "FAILED"
                _error.value = e.message ?: "Unable to start KHQR payment"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun startPolling(paymentId: Long) {
        pollingJob = viewModelScope.launch {
            while (_status.value !in listOf("PAID", "EXPIRED", "FAILED")) {
                delay(5000)
                try {
                    val response = repository.getStatus(paymentId)
                    _payment.value = response
                    _status.value = response.status
                } catch (e: Exception) {
                    _error.value = e.message ?: "Unable to refresh payment status"
                }
            }
        }
    }
}
