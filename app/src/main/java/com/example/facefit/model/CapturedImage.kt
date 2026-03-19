package com.example.facefit.model

data class CapturedImage(
    val imageId: String = "",
    val userId: String = "",
    val imagePath: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
