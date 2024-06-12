package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.Predict

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.

        val predict = if (Build.VERSION.SDK_INT >= 34) {
            intent.getParcelableExtra(PREDICT, Predict::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(PREDICT)
        }
        with(binding) {
            resultImage.setImageURI(predict?.imageUri)
            if (predict != null) {
                val scorePercentage = predict.score.times(100)
                val formattedScore = String.format("%.2f", scorePercentage)
                val message = "Label: ${predict.label} Score: $formattedScore%"

                resultText.text = message
            }
        }
    }

    companion object {
        const val PREDICT = "predict"
    }

}