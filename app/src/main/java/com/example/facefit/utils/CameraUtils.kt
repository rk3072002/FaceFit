package com.example.facefit.utils


import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.facefit.model.FaceData
import com.example.facefit.model.FaceLandmarkData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

object CameraUtils {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setMinFaceSize(0.1f)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(options)

    @androidx.camera.core.ExperimentalGetImage
    fun processImageProxy(
        imageProxy: ImageProxy,
        onFaceDetected: (List<FaceData>) -> Unit
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    val faceDataList = faces.map { face ->
//                        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
//                        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
//                        val nose = face.getLandmark(FaceLandmark.NOSE_BASE)?.position
                        FaceData(
                            boundingBox = face.boundingBox,
                            landmark = FaceLandmarkData(
                                leftEyeX = face.getLandmark(FaceLandmark.LEFT_EYE)?.position?.x ?: 0f,
                                leftEyeY = face.getLandmark(FaceLandmark.LEFT_EYE)?.position?.y ?: 0f,
                                rightEyeX = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position?.x ?: 0f,
                                rightEyeY = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position?.y ?: 0f,
                                noseX = face.getLandmark(FaceLandmark.NOSE_BASE)?.position?.x ?: 0f,
                                noseY = face.getLandmark(FaceLandmark.NOSE_BASE)?.position?.y ?: 0f,
                                mouthX = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position?.x ?: 0f,
                                mouthY = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position?.y ?: 0f
//                                leftEyeX = leftEye?.x ?: 0f,
//                                leftEyeY = leftEye?.y ?: 0f,
//                                rightEyeX = rightEye?.x ?: 0f,
//                                rightEyeY = rightEye?.y ?: 0f,
//                                noseX = nose?.x ?: 0f,
//                                noseY = nose?.y ?: 0f
                            ),
                            face = face
                        )
                    }
                    onFaceDetected(faceDataList)
                }
                .addOnFailureListener {
                    onFaceDetected(emptyList())
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
            onFaceDetected(emptyList())
        }
    }
}
