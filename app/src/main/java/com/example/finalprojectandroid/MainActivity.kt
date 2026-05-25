package com.example.finalprojectandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
        },
        label = "ScreenTransition"
    ) { currentScreen ->
        when (currentScreen) {
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
                    onBack = { screen = AppScreen.Main },
                    onViewCart = {
                        selectedTab = MainTab.Cart
                        screen = AppScreen.Main
                    }
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
                        selectedTab = MainTab.Home
                        screen = AppScreen.Main
                    },
                    onSuccess = { screen = AppScreen.Success }
                )
            }

            AppScreen.Success -> PaymentSuccessScreen(
                onBackToHome = {
                    selectedTab = MainTab.Home
                    screen = AppScreen.Main
                }
            )
        }
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
            // Premium Floating Dock Navigation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 12.dp,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MainTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onTabSelected(tab) }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val iconSize by animateDpAsState(if (isSelected) 28.dp else 24.dp, label = "icon")
                                    
                                    BadgedBox(
                                        badge = {
                                            if (tab == MainTab.Cart && cartCount > 0) {
                                                Badge(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = Color.White
                                                ) { Text(cartCount.toString()) }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.label,
                                            modifier = Modifier.size(iconSize),
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary 
                                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                        )
                                    }
                                    
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        // content respects padding to avoid overlap
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                MainTab.Home -> HomeDashboardScreen(
                    productViewModel = productViewModel,
                    cartViewModel = cartViewModel,
                    onShopClick = { onTabSelected(MainTab.Shop) }
                )

                MainTab.Shop -> ProductListScreen(
                    viewModel = productViewModel,
                    onProductClick = onProductClick,
                    onCartClick = { onTabSelected(MainTab.Cart) }
                )

                MainTab.Cart -> CartScreen(
                    viewModel = cartViewModel,
                    onBack = { onTabSelected(MainTab.Shop) },
                    onCheckout = onCheckout,
                    showBackButton = false
                )

                MainTab.Settings -> SettingsScreen(onLogout = onLogout)
            }
        }
    }
}

private enum class AppScreen {
    Login, SignUp, Main, ProductDetail, Checkout, Payment, Success
}

private enum class MainTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Shop("Shop", Icons.Default.Storefront),
    Cart("Cart", Icons.Default.ShoppingCart),
    Settings("Settings", Icons.Default.Settings)
}
