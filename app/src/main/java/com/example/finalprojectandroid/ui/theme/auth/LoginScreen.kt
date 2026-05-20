package com.example.finalprojectandroid.ui.theme.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateSignUp: () -> Unit,
    onNavigateHome: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        if (viewModel.errorMessage.value != null) {
            Text(viewModel.errorMessage.value!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // We navigate to home page immediately to avoid waiting for slow backend response.
        // The authentication still runs in the background.
        Button(
            onClick = {
                onNavigateHome()
                viewModel.login(email, password, {})
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        if (viewModel.isLoading.value) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        TextButton(onClick = onNavigateSignUp) {
            Text("Don't have an account? Sign Up")
        }
    }
}
