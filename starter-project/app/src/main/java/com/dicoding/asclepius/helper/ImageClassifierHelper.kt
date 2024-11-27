package com.dicoding.asclepius.helper

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier



class ImageClassifierHelper(
    var threshold: Float = 0.1f,
    var maxResults: Int = 3,
    val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private lateinit var classifier: ImageClassifier

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        try {

            val baseOptions = BaseOptions.builder()
                .setNumThreads(4)
                .build()


            val options = ImageClassifier.ImageClassifierOptions.builder()
                .setBaseOptions(baseOptions)
                .setMaxResults(maxResults)
                .setScoreThreshold(threshold)
                .build()


            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                options
            )

            println("Image Classifier setup successfully.")

        } catch (e: Exception) {
            classifierListener?.onError("Error setting up image classifier: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
    fun classifyStaticImage(imageUri: Uri, contentResolver: ContentResolver) {
        // TODO: mengklasifikasikan imageUri dari gambar statis.
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888,true)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            }

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val tensorImage = TensorImage.fromBitmap(resizedBitmap)
            val classifications = classifier.classify(tensorImage)
            val topResult = classifications.firstOrNull()?.categories?.firstOrNull()
            val label = topResult?.label ?: "Unknown"
            val confidence = topResult?.score ?: 0f

            println("Predicted Cancer: $label, Confidence: ${confidence * 100}%")


            classifierListener?.onResults(classifications, 0)

        } catch (e: Exception) {
            classifierListener?.onError("Error classifying image: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }



}