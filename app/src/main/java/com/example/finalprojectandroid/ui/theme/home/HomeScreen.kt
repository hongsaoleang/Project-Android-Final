package com.example.finalprojectandroid.ui.theme.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalprojectandroid.data.repository.AuthRepository

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val repository = AuthRepository()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Home Screen!", style = MaterialTheme.typography.headlineMedium)
        Text("User ID: ${repository.getCurrentUserUid()}")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            repository.logout()
            onLogout()
        }) {
            Text("Logout")
        }
    }
}