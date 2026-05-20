package com.example.finalprojectandroid.data.api

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // 1. Clean OkHttpClient WITHOUT the Firebase Interceptor (For Payments and Public Data)
    private val baseClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 2. Authenticated OkHttpClient WITH the Firebase Interceptor (For Orders/Sensitive Data)
    private val authClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val user = FirebaseAuth.getInstance().currentUser

                // Get the token synchronously. Interceptors run on a background thread.
                val token: String? = try {
                    user?.let { Tasks.await(it.getIdToken(false)) }?.token
                } catch (e: Exception) {
                    Log.e("RetrofitClient", "Failed to get Firebase token", e)
                    null
                }
                
                Log.d("RetrofitClient", "Attempting to send request to: ${chain.request().url}")
                Log.d("RetrofitClient", "Firebase User: ${user?.uid ?: "null"}")
                Log.d("RetrofitClient", "Firebase Token obtained: ${token?.take(10)}... (truncated)")

                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    // 3. Retrofit Instance for Authenticated Requests
    private val retrofitAuth by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 4. Retrofit Instance for Public/Payment Requests (No Bearer Token)
    private val retrofitPublic by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(baseClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 5. Expose APIs
    // Changed productApi to use retrofitPublic to bypass token requirement
    val productApi: ProductApiService by lazy { retrofitPublic.create(ProductApiService::class.java) }
    val orderApi: OrderApiService by lazy { retrofitAuth.create(OrderApiService::class.java) }
    val paymentApi: PaymentApiService by lazy { retrofitPublic.create(PaymentApiService::class.java) }
}
