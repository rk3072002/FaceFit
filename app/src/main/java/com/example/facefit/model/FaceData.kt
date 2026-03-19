package com.example.facefit.model

import android.graphics.Rect

data class FaceData(
    val boundingBox: Rect,
    val landmark: FaceLandmarkData,
    val face: com.google.mlkit.vision.face.Face?
)
