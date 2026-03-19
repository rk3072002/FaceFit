package com.example.facefit.repositry

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.facefit.model.FaceData
import com.example.facefit.utils.CameraUtils
import com.example.facefit.utils.FaceOverlayView
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors



//class CameraRepository {
//
//    private var imageCapture: ImageCapture? = null
//    private lateinit var cameraExecutor: ExecutorService
//    private var faceDetectionListener: ((List<FaceData>) -> Unit)? = null
//
//    fun setFaceDetectionListener(listener: (List<FaceData>) -> Unit) {
//        faceDetectionListener = listener
//    }
//
//    fun startCamera(
//        context: Context,
//        lifecycleOwner: LifecycleOwner,
//        previewView: PreviewView,
//        faceOverlayView: FaceOverlayView
//    ) {
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            // Preview
//            val preview = Preview.Builder().build().also {
//                it.setSurfaceProvider(previewView.surfaceProvider)
//            }
//            previewView.scaleX = -1f // Front camera mirror
//
//            // Image Capture
//            imageCapture = ImageCapture.Builder().build()
//
//            // Image Analysis
//            val imageAnalysis = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//
//            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
//                CameraUtils.processImageProxy(imageProxy) { faces ->
//                    faceDetectionListener?.invoke(faces)
//                }
//            }
//
//            // Select front camera
//            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    lifecycleOwner,
//                    cameraSelector,
//                    preview,
//                    imageCapture,
//                    imageAnalysis
//                )
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }, ContextCompat.getMainExecutor(context))
//    }
//
//    fun takePhoto(context: Context, onImageCaptured: (Uri?) -> Unit) {
//        val imageCapture = imageCapture ?: return
//
//        val photoFile = File(context.cacheDir, "facefit_${System.currentTimeMillis()}.jpg")
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(context),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    onImageCaptured(Uri.fromFile(photoFile))
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    exception.printStackTrace()
//                    onImageCaptured(null)
//                }
//            }
//        )
//    }
//
//    fun stopCamera() {
//        if (::cameraExecutor.isInitialized) {
//            cameraExecutor.shutdown()
//        }
//    }
//}
//}
//class CameraRepository {
//    private var imageCapture: ImageCapture? = null
//    private lateinit var cameraExecutor: ExecutorService
//
//    fun startCamera(
//        context: Context,
//        lifecycleOwner: LifecycleOwner,
//        previewView: PreviewView,
//        faceOverlayView: FaceOverlayView
//    ) {
//        cameraExecutor = Executors.newSingleThreadExecutor()
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            val preview = Preview.Builder().build().also {
//                it.setSurfaceProvider(previewView.surfaceProvider)
//            }
//            previewView.scaleX = -1f // front camera mirror
//
//            imageCapture = ImageCapture.Builder().build()
//
//            val imageAnalysis = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//
//            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
//                CameraUtils.processImageProxy(imageProxy) { faces ->
//                    if (!faces.isNullOrEmpty()) {
//                        val largestFace: Face? = faces.maxByOrNull { face: Face ->
//                            ((face.boundingBox.width()) * (face.boundingBox.height()))
//                        }
//
//                        faceOverlayView.post {
//                            faceOverlayView.setTransform(imageProxy.width, imageProxy.height)
//                            largestFace?.let { faceOverlayView.setFaceData(it) }
//                        }
//                    }else{
//                        faceOverlayView.post {
//                           // faceOverlayView.clearFaceData()
//                            Log.d("face debug", "this will be face debug")
//                        }
//                    }
//                }
//            }
//
//            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    lifecycleOwner,
//                    cameraSelector,
//                    preview,
//                    imageCapture,
//                    imageAnalysis
//                )
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }, ContextCompat.getMainExecutor(context))
//    }
//
//    fun takePhoto(context: Context, onImageCaptured: (Uri?) -> Unit) {
//        val imageCapture = imageCapture ?: return
//        val photoFile = File(context.cacheDir, "facefit_${System.currentTimeMillis()}.jpg")
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(context),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    onImageCaptured(Uri.fromFile(photoFile))
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    exception.printStackTrace()
//                    onImageCaptured(null)
//                }
//            }
//        )
//    }
//}

class CameraRepository {
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    fun startCamera(context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView, faceOverlayView: FaceOverlayView) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            previewView.scaleX = -1f

            imageCapture = ImageCapture.Builder().build()

            // REAL-TIME ANALYSIS
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(
                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                )
                .build()
            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                CameraUtils.processImageProxy(imageProxy) { face ->
                    faceOverlayView.post {
                        faceOverlayView.setTransform(
                            imageProxy.width,
                            imageProxy.height
                        )
                        if (face != null) {
                            faceOverlayView.setFaceData(face)
                        }
                    }
                }
                }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(context: Context, onImageCaptured: (Uri?) -> Unit) {
        val imageCapture = imageCapture ?: return
        val photoFile = File(context.cacheDir, "facefit_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageCaptured(Uri.fromFile(photoFile))
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onImageCaptured(null)
                }
            }
        )
    }
}