package com.example.finalprojectandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectandroid.ui.cart.CartScreen
import com.example.finalprojectandroid.ui.cart.CheckoutScreen
import com.example.finalprojectandroid.ui.main.HomeDashboardScreen
import com.example.finalprojectandroid.ui.main.PaymentSuccessScreen
import com.example.finalprojectandroid.ui.main.SettingsScreen
import com.example.finalprojectandroid.ui.payment.PaymentScreen
import com.example.finalprojectandroid.ui.product.ProductDetailScreen
import com.example.finalprojectandroid.ui.product.ProductListScreen
import com.example.finalprojectandroid.ui.theme.FinalProjectAndroidTheme
import com.example.finalprojectandroid.ui.theme.auth.AuthViewModel
import com.example.finalprojectandroid.ui.theme.auth.LoginScreen
import com.example.finalprojectandroid.ui.theme.auth.SignUpScreen
import com.example.finalprojectandroid.viewmodel.CartViewModel
import com.example.finalprojectandroid.viewmodel.PaymentViewModel
import com.example.finalprojectandroid.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FinalProjectAndroidTheme(dynamicColor = false) {
                EcommerceApp()
            }
        }
    }
}

@Composable
private fun EcommerceApp() {
    val authViewModel: AuthViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val paymentViewModel: PaymentViewModel = viewModel()

    var screen by remember { mutableStateOf(AppScreen.Login) }
    var selectedTab by remember { mutableStateOf(MainTab.Home) }
    var selectedProductId by remember { mutableStateOf<Long?>(null) }
    var selectedOrderId by remember { mutableStateOf<Long?>(null) }

    when (screen) {
        AppScreen.Login -> LoginScreen(
            viewModel = authViewModel,
            onNavigateSignUp = { screen = AppScreen.SignUp },
            onNavigateHome = {
                selectedTab = MainTab.Home
                screen = AppScreen.Main
            }
        )

        AppScreen.SignUp -> SignUpScreen(
            viewModel = authViewModel,
            onNavigateLogin = { screen = AppScreen.Login },
            onNavigateHome = {
                selectedTab = MainTab.Home
                screen = AppScreen.Main
            }
        )

        AppScreen.Main -> MainShell(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            productViewModel = productViewModel,
            cartViewModel = cartViewModel,
            onProductClick = { id ->
                selectedProductId = id
                screen = AppScreen.ProductDetail
            },
            onCheckout = { screen = AppScreen.Checkout },
            onLogout = { screen = AppScreen.Login }
        )

        AppScreen.ProductDetail -> selectedProductId?.let { id ->
            ProductDetailScreen(
                productId = id,
                viewModel = productViewModel,
                onBack = { screen = AppScreen.Main }
            )
        }

        AppScreen.Checkout -> CheckoutScreen(
            viewModel = cartViewModel,
            onBack = {
                selectedTab = MainTab.Cart
                screen = AppScreen.Main
            },
            onPaymentReady = { orderId ->
                selectedOrderId = orderId
                screen = AppScreen.Payment
            }
        )

        AppScreen.Payment -> selectedOrderId?.let { orderId ->
            PaymentScreen(
                orderId = orderId,
                viewModel = paymentViewModel,
                onBack = {
                    selectedTab = MainTab.Cart
                    screen = AppScreen.Main
                },
                onSuccess = { screen = AppScreen.Success }
            )
        }

        AppScreen.Success -> PaymentSuccessScreen(
            onContinueShopping = {
                selectedTab = MainTab.Shop
                screen = AppScreen.Main
            }
        )
    }
}

@Composable
private fun MainShell(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    onProductClick: (Long) -> Unit,
    onCheckout: () -> Unit,
    onLogout: () -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        icon = {
                            if (tab == MainTab.Cart && cartCount > 0) {
                                BadgedBox(badge = { Badge { Text(cartCount.toString()) } }) {
                                    Icon(tab.icon, contentDescription = tab.label)
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.label)
                            }
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        val contentModifier = Modifier.padding(padding)
        when (selectedTab) {
            MainTab.Home -> androidx.compose.foundation.layout.Box(contentModifier) {
                HomeDashboardScreen(
                    productViewModel = productViewModel,
                    cartViewModel = cartViewModel,
                    onShopClick = { onTabSelected(MainTab.Shop) }
                )
            }

            MainTab.Shop -> androidx.compose.foundation.layout.Box(contentModifier) {
                ProductListScreen(
                    viewModel = productViewModel,
                    onProductClick = onProductClick,
                    onCartClick = { onTabSelected(MainTab.Cart) }
                )
            }

            MainTab.Cart -> androidx.compose.foundation.layout.Box(contentModifier) {
                CartScreen(
                    viewModel = cartViewModel,
                    onBack = { onTabSelected(MainTab.Shop) },
                    onCheckout = onCheckout,
                    showBackButton = false
                )
            }

            MainTab.Settings -> androidx.compose.foundation.layout.Box(contentModifier) {
                SettingsScreen(onLogout = onLogout)
            }
        }
    }
}

private enum class AppScreen {
    Login,
    SignUp,
    Main,
    ProductDetail,
    Checkout,
    Payment,
    Success
}

private enum class MainTab(
    val label: String,
    val icon: ImageVector
) {
    Home("Home", Icons.Default.Home),
    Shop("Shop", Icons.Default.Storefront),
    Cart("Cart", Icons.Default.ShoppingCart),
    Settings("Settings", Icons.Default.Settings)
}
