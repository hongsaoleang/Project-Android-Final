package com.example.finalprojectandroid.viewmodel

import android.util.Log
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
            _error.value = null
            pollingJob?.cancel()
            try {
                val response = repository.createPayment(orderId)
                _payment.value = response
                _status.value = response.status
                _error.value = null
                
                // Automatically start polling in the background if PENDING
                if (response.status == "PENDING") {
                    startPolling(response.id)
                }
            } catch (e: Exception) {
                _status.value = "FAILED"
                _error.value = e.message ?: "Unable to start KHQR payment"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun startPolling(paymentId: Long) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            // Poll every 2 seconds quietly in the background
            while (_status.value == "PENDING") {
                delay(2000)
                try {
                    val response = repository.checkBakong(paymentId)
                    _payment.value = response
                    _status.value = response.status
                } catch (e: Exception) {
                    Log.e("PaymentViewModel", "Polling refresh failed: ${e.message}")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }

    // Keep this for manual refresh if needed, but it won't trigger the main loading circle
    fun updatePaymentStatus(paymentId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.checkBakong(paymentId)
                _payment.value = response
                _status.value = response.status
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Manual refresh failed: ${e.message}")
            }
        }
    }
}
