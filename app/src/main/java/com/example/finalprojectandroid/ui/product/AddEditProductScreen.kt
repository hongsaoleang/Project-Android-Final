package com.example.finalprojectandroid.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalprojectandroid.data.model.CreateProductRequest
import com.example.finalprojectandroid.data.model.UpdateProductRequest
import com.example.finalprojectandroid.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    productId: Int? = null,
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
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (productId == null) {
                        viewModel.createProduct(CreateProductRequest(name, description, price.toDoubleOrNull() ?: 0.0, imageUrl, stock.toIntOrNull() ?: 0), onSuccess)
                    } else {
                        viewModel.updateProduct(productId, UpdateProductRequest(name, description, price.toDoubleOrNull() ?: 0.0, imageUrl, stock.toIntOrNull() ?: 0), onSuccess)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (productId == null) "Create" else "Update")
            }
        }
    }
}
