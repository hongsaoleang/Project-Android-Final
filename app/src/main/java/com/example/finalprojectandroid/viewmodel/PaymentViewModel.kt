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

    // Holds the active inner-poller job so it can be cancelled before starting fresh
    private var pollingJob: Job? = null
    private var currentPaymentId: Long? = null

    fun startPayment(orderId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Cancel any previously running inner-poller job so it
            // doesn't hold stale state on re-entry.
            pollingJob?.cancel()
            pollingJob = null

            try {
                val response = repository.createPayment(orderId)
                _payment.value = response
                _status.value = response.status
                _error.value = null
                currentPaymentId = response.id

                Log.d("PaymentFlow", "Payment Started. ID: ${response.id}, Status: ${response.status}")

                // If already paid, no need to poll
                if (response.status == "PAID") {
                    return@launch
                }

                // Start polling for payment confirmation
                startPolling(response.id)
            } catch (e: Exception) {
                _status.value = "FAILED"
                _error.value = e.message ?: "Unable to start KHQR payment"
                Log.e("PaymentFlow", "Failed to start payment: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun startPolling(paymentId: Long) {
        pollingJob = viewModelScope.launch {
            while (_status.value == "PENDING") {
                delay(2000)
                try {
                    val refreshed = repository.checkBakong(paymentId)
                    _payment.value = refreshed
                    _status.value = refreshed.status
                    Log.d("PaymentFlow", "Polling payment $paymentId: ${refreshed.status}")
                } catch (e: Exception) {
                    Log.e("PaymentCheck", "Check-bakong failed for paymentId=$paymentId: ${e.message}")
                }
            }
        }
    }

    /** Check if payment was already made - useful when returning from banking app */
    fun refreshPaymentStatus() {
        val paymentId = currentPaymentId
        if (paymentId == null || _status.value == "PAID") return

        viewModelScope.launch {
            try {
                val refreshed = repository.checkBakong(paymentId)
                _payment.value = refreshed
                _status.value = refreshed.status
                Log.d("PaymentFlow", "Refreshed payment $paymentId: ${refreshed.status}")
            } catch (e: Exception) {
                Log.e("PaymentCheck", "Refresh failed for paymentId=$paymentId: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
