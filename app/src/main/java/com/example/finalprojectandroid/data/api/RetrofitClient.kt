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

    private val authClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val user = FirebaseAuth.getInstance().currentUser
                
                // Get the token synchronously. Interceptors run on a background thread.
                val token = try {
                    user?.let { Tasks.await(it.getIdToken(true)).token }
                } catch (e: Exception) {
                    Log.e("RetrofitClient", "Failed to get Firebase token", e)
                    null
                }

                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productApi: ProductApiService by lazy { retrofit.create(ProductApiService::class.java) }
    val orderApi: OrderApiService by lazy { retrofit.create(OrderApiService::class.java) }
    val paymentApi: PaymentApiService by lazy { retrofit.create(PaymentApiService::class.java) }
}
