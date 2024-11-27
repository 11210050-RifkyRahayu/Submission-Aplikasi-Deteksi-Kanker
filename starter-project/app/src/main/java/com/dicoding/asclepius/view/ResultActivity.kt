package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val confidence = intent.getFloatExtra(EXTRA_CONFIDENCE,0f)
        val imageUriString= intent.getStringExtra(EXTRA_IMAGE_URI)
        val errorMessage = intent.getStringExtra(EXTRA_ERROR_MESSAGE)

        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding.resultImage.setImageURI(imageUri)
        }?:run {
            binding.resultImage.setImageResource(R.drawable.ic_place_holder)
        }
        if (errorMessage != null) {
            binding.resultText.text = "Error processing the image: $errorMessage"
        } else {
            val resultCancer = if (confidence < 0.57f) {
                "The Result Are: \nNon Cancer: ${"%.2f".format(confidence * 100)}%"
            } else {
                "The Result Are: \nCancer: ${"%.2f".format(confidence * 100)}%"
            }
            binding.resultText.text = resultCancer

        }
    }
    companion object {

        const val EXTRA_CONFIDENCE = "extra_confidence"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_ERROR_MESSAGE = "extra_error_message"
    }
}