package com.example.facefit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.facefit.model.FaceData
import com.example.facefit.model.Filter
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark




class FaceOverlayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null ) : View(context, attrs) {
//    lateinit var faceData: List<FaceData>

    //lateinit var faceData: Face
    private var scaleX = 1f
    private var scaleY = 1f
    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

   private var faceDataList: List<FaceData> = emptyList()

    // dummy filter bitmap (replace later with real drawable)
    private var filterBitmap: Bitmap? = null

    private var lastFaceData: List<FaceData> = emptyList()
    fun setFaceData(faces: List<FaceData>) {
        this.faceDataList = faces
        this.lastFaceData = faces
       // this.lastFace = faces
        invalidate()
    }
    fun getLastFaceData(): List<FaceData> {
        return lastFaceData
    }
    fun getFilterBitmap(): Bitmap? {
        return filterBitmap
    }

    var filterType = "NONE"

    fun setFilter(bitmap: Bitmap, type: String) {
        this.filterBitmap = bitmap
        this.filterType = type
        invalidate()
    }


override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.scale(-1f, 1f, width / 2f, height / 2f)

    val facesToDraw = if (faceDataList.isNotEmpty()) faceDataList else lastFaceData
    if (facesToDraw.isEmpty()) return

    for (f in facesToDraw) {

        val leftEyeX = f.landmark.leftEyeX * scaleX
        val leftEyeY = f.landmark.leftEyeY * scaleY

        val rightEyeX = f.landmark.rightEyeX * scaleX
        val rightEyeY = f.landmark.rightEyeY * scaleY

        val noseX = f.landmark.noseX * scaleX
        val noseY = f.landmark.noseY * scaleY

        val bounds = f.boundingBox

        filterBitmap?.let { bitmap ->

            when (filterType) {

                "GLASSES" -> {
                    val faceWidth = bounds.width() * scaleX
                    val width = (faceWidth * 1.5).toInt()
                    val height = (width * bitmap.height / bitmap.width)

                    val scaled = Bitmap.createScaledBitmap(bitmap, width, height, false)

                    canvas.drawBitmap(
                        scaled,
                        leftEyeX - width / 4,
                        leftEyeY - height / 2,
                        null
                    )
                }

                "NOSE" -> {
                    val size = (bounds.width() * scaleX).toInt()

                    val scaled = Bitmap.createScaledBitmap(bitmap, size, size, false)

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
                        (noseY),
                        bounds.right * scaleX,
                        bounds.bottom * scaleY
                    )

                    val scaled = Bitmap.createScaledBitmap(
                        bitmap,
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
    }
}
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
    fun setTransform(imageWidth: Int, imageHeight: Int) {
        scaleX = width.toFloat() / imageWidth.toFloat()
        scaleY = height.toFloat() / imageHeight.toFloat()
    }


}
