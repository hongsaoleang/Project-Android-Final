package com.example.finalprojectandroid.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.finalprojectandroid.data.model.CreateProductRequest
import com.example.finalprojectandroid.data.model.UpdateProductRequest
import com.example.finalprojectandroid.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    productId: Long? = null,
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val existingProduct = productId?.let { id -> products.find { it.id == id } }

    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var description by remember { mutableStateOf(existingProduct?.description ?: "") }
    var price by remember { mutableStateOf(existingProduct?.price?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(existingProduct?.imageUrl ?: "") }
    var stock by remember { mutableStateOf(existingProduct?.stock?.toString() ?: "") }

    LaunchedEffect(existingProduct?.id) {
        existingProduct?.let {
            name = it.name
            description = it.description
            price = it.price.toString()
            imageUrl = it.imageUrl
            stock = it.stock.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "Add Product" else "Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }
            )
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
            )
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Available Stock") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            val isFormValid = name.isNotBlank() && price.toDoubleOrNull() != null && stock.toIntOrNull() != null
            
            Button(
                onClick = {
                    if (productId == null) {
                        viewModel.createProduct(CreateProductRequest(name, description, price.toDoubleOrNull() ?: 0.0, imageUrl, stock.toIntOrNull() ?: 0), onSuccess)
                    } else {
                        viewModel.updateProduct(productId, UpdateProductRequest(name, description, price.toDoubleOrNull() ?: 0.0, imageUrl, stock.toIntOrNull() ?: 0), onSuccess)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(if (productId == null) "Create" else "Update")
            }
        }
    }
}
