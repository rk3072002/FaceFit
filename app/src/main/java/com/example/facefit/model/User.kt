package com.example.facefit.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
