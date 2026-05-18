package com.example.finalprojectandroid.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val createAt: Long = System.currentTimeMillis()
)