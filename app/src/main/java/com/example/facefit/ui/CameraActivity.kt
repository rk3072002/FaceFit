package com.example.facefit.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.facefit.R
import com.example.facefit.adapter.FilterAdapter
import com.example.facefit.databinding.ActivityCameraBinding
import com.example.facefit.model.Filter
import com.example.facefit.viewmodel.CameraViewModel


class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private val cameraViewModel: CameraViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCameraPermission()
        setupListeners()
        observeCapturedImage()
        setupFilters()

        binding.filterRecycler.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.faceOverlay.apply {
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
            isEnabled = false
            setOnTouchListener { _, _ -> false }
        }

        binding.filterRecycler.bringToFront()
        binding.btnCapture.bringToFront()

    }

    private fun setupListeners() {
        binding.btnCapture.setOnClickListener {
            binding.btnCapture.isEnabled = false
            Log.d("CLICK_TEST", "Capture button clicked")
            cameraViewModel.capturePhoto(this)
        }
    }

    private fun observeCapturedImage() {
        cameraViewModel.capturedImage.observe(this) { uri: Uri? ->
            binding.btnCapture.isEnabled = true
            if (uri != null) {
                Toast.makeText(this, "Image Captured: $uri", Toast.LENGTH_SHORT).show()
                // you can apply filters to the image preview
                binding.previewView.visibility = View.GONE
                binding.capturedImageView.visibility = View.VISIBLE
                binding.capturedImageView.setImageURI(uri)

                saveImageWithFilter(uri)
            } else {
                Toast.makeText(this, "Capture failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun startCamera() {
        cameraViewModel.startCamera( this, this, binding.previewView, binding.faceOverlay )
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch( Manifest.permission.CAMERA )
        }
    }
    private val requestPermissionLauncher = registerForActivityResult( ActivityResultContracts.RequestPermission() ) {
        isGranted: Boolean ->
        if (isGranted) { startCamera()
        } else {
            Toast.makeText( this, "Camera permission required", Toast.LENGTH_SHORT ).show()
        }
    }
    private fun setupFilters() {

        val filters = listOf(
            Filter(0, "GLASSES", R.drawable.glasses, R.drawable.glasses),
            Filter(1, "BEARD", R.drawable.beard, R.drawable.beard),
            Filter(2, "NOSE", R.drawable.mask, R.drawable.mask)
        )

        val adapter = FilterAdapter(filters) { filter ->
            Log.d("CLICK_DEBUG", "Clicked filter: ${filter.name}")

            val bitmap = BitmapFactory.decodeResource(
                resources,
                filter.overlayRes
            )

            Log.d("FILTER_DEBUG", "ResId: ${filter.overlayRes}")
            Log.d("BITMAP_DEBUG", "Bitmap is null? ${bitmap == null}")
        if (bitmap != null) {
                Log.d("FILTER_APPLY", "Applying filter now")
                binding.faceOverlay.setFilter(bitmap, filter.name.uppercase())
                binding.faceOverlay.postInvalidate()
            }else{
            Toast.makeText(this, "Invalid filter image", Toast.LENGTH_SHORT).show()
        }


        }

        binding.filterRecycler.adapter = adapter
    }


    private fun saveBitmapToGallery(bitmap: Bitmap): Uri? {
        val filename = "FaceFit_${System.currentTimeMillis()}.png"
        val savedUri: Uri? = contentResolver.insert(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            android.content.ContentValues().apply {
                put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png")
            }
        )
        savedUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }
        return savedUri
    }

    private fun drawFilterOnBitmap(originalBitmap: Bitmap): Bitmap {

        val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)

        val faces = binding.faceOverlay.getLastFaceData()
        val filterBitmap = binding.faceOverlay.getFilterBitmap()
        val filterType = binding.faceOverlay.filterType

        if (faces.isEmpty() || filterBitmap == null) return resultBitmap

        for (f in faces) {

            val scaleX = originalBitmap.width.toFloat() / binding.faceOverlay.width
            val scaleY = originalBitmap.height.toFloat() / binding.faceOverlay.height

            val leftEyeX = f.landmark.leftEyeX * scaleX
            val leftEyeY = f.landmark.leftEyeY * scaleY

            val rightEyeX = f.landmark.rightEyeX * scaleX
            val rightEyeY = f.landmark.rightEyeY * scaleY

            val noseX = f.landmark.noseX * scaleX
            val noseY = f.landmark.noseY * scaleY

            val bounds = f.boundingBox

            when (filterType) {

                "GLASSES" -> {
                    val faceWidth = bounds.width() * scaleX
                    val width = (faceWidth * 1.5).toInt()
                    val height = (width * filterBitmap.height / filterBitmap.width)

                    val scaled = Bitmap.createScaledBitmap(filterBitmap, width, height, false)

                    canvas.drawBitmap(
                        scaled,
                        leftEyeX - width / 4,
                        leftEyeY - height / 2,
                        null
                    )
                }

                "NOSE" -> {
                    val size = (bounds.width() * scaleX).toInt()
                    val scaled = Bitmap.createScaledBitmap(filterBitmap, size, size, false)

                    canvas.drawBitmap(
                        scaled,
                        noseX - size / 2,
                        noseY,
                        null
                    )
                }

                "BEARD" -> {
                    val beardRect = RectF(
                        bounds.left * scaleX,
                        noseY,
                        bounds.right * scaleX,
                        bounds.bottom * scaleY
                    )

                    val scaled = Bitmap.createScaledBitmap(
                        filterBitmap,
                        beardRect.width().toInt(),
                        beardRect.height().toInt(),
                        false
                    )

                    canvas.drawBitmap(
                        scaled,
                        beardRect.left,
                        beardRect.top,
                        null
                    )
                }
            }
        }

        return resultBitmap
    }
    private fun saveImageWithFilter(originalUri: Uri) {

        var originalBitmap =
            BitmapFactory.decodeStream(contentResolver.openInputStream(originalUri))
        originalBitmap = rotateBitmapIfRequired(originalBitmap, originalUri)

        val resultBitmap = drawFilterOnBitmap(originalBitmap)

        val savedUri = saveBitmapToGallery(resultBitmap)

        Log.d("SAVE_DEBUG", "Face count: ${binding.faceOverlay.getLastFaceData().size}")
        Log.d("SAVE_DEBUG", "Filter type: ${binding.faceOverlay.filterType}")

        Toast.makeText(this, "Saved with filter: $savedUri", Toast.LENGTH_SHORT).show()
    }
    private fun rotateBitmapIfRequired(bitmap: Bitmap, uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        val exif = androidx.exifinterface.media.ExifInterface(inputStream!!)

        val orientation = exif.getAttributeInt(
            androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}