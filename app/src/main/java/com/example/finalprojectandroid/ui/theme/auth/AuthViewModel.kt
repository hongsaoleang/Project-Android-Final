package com.example.finalprojectandroid.ui.theme.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectandroid.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel (private val repository: AuthRepository = AuthRepository()): ViewModel(){
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun signUp(name: String, email: String, password: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            val result = repository.registerUser(name, email, password, phone)
            isLoading.value = false
            if(result.isSuccess) onSuccess()
            else errorMessage.value = result.exceptionOrNull()?.message
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            val result = repository.loginUser(email, password)
            isLoading.value = false
            if(result.isSuccess) onSuccess()
            else errorMessage.value = result.exceptionOrNull()?.message
        }
    }
}