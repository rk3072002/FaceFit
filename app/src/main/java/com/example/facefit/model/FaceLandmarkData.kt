package com.example.facefit.model

data class FaceLandmarkData(
    val leftEyeX: Float = 0f,
    val leftEyeY: Float = 0f,
    val rightEyeX: Float = 0f,
    val rightEyeY: Float = 0f,
    val noseX: Float = 0f,
    val noseY: Float = 0f,
    val mouthX: Float = 0f,
    val mouthY: Float = 0f
)
