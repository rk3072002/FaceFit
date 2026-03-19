package com.example.facefit.viewmodel

import android.content.Context
import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.facefit.repositry.CameraRepository
import com.example.facefit.utils.FaceOverlayView

class CameraViewModel : ViewModel() {
    private val repository = CameraRepository()
    private val _capturedImageUri = MutableLiveData<Uri?>()
    val capturedImage: LiveData<Uri?> = _capturedImageUri

    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        faceOverlayView: FaceOverlayView
    ) {
        repository.startCamera(
            context,
            lifecycleOwner,
            previewView,
            faceOverlayView
        )
    }

        fun capturePhoto(context: Context) {
            repository.takePhoto(context) { uri ->
                _capturedImageUri.postValue(uri)
            }
        }
    }
