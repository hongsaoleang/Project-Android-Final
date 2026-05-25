package com.example.finalprojectandroid.data.repository

import android.util.Log
import com.example.finalprojectandroid.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerUser(name: String, email: String, pass: String, phone: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")
            val user = User(uid = uid, name = name, email = email, phone = phone)
            
            try {
                db.collection("users").document(uid).set(user).await()
                Log.d("AuthRepository", "User profile successfully created for UID: $uid")
                Result.success(Unit)
            } catch (dbError: Exception) {
                Log.e("AuthRepository", "Firestore failed, deleting Auth account...", dbError)
                result.user?.delete()?.await()
                Result.failure(dbError)
            }
        } catch (authError: Exception) {
            Log.e("AuthRepository", "Auth account creation failed", authError)
            Result.failure(authError)
        }
    }

    suspend fun loginUser(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Log.d("AuthRepository", "Login successful for: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed for: $email", e)
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        Log.d("AuthRepository", "User logged out successfully")
    }

    fun getCurrentUserUid(): String? = auth.currentUser?.uid

    /** Fetches the full profile of the currently logged-in user */
    suspend fun getUserProfile(): User? {
        val uid = getCurrentUserUid() ?: return null
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to fetch user profile", e)
            null
        }
    }
}
