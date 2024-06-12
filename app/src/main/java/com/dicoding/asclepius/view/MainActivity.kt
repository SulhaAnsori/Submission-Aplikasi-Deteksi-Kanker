package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.helper.Predict
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            galleryButton.setOnClickListener{startGallery()}
            analyzeButton.setOnClickListener{analyzeImage()}
        }

    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    // TODO: Menganalisa gambar yang berhasil ditampilkan.
    private fun analyzeImage() {
        currentImageUri?.let { imageUri ->
            ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            results?.let { classification ->
                                if (classification.isNotEmpty() && classification[0].categories.isNotEmpty()){
                                    classification[0].categories.maxByOrNull { it.score }?.let {
                                        val predict = Predict(
                                            imageUri = imageUri,
                                            label = it.label,
                                            score = it.score,
                                        )
                                        moveToResult(predict)
                                    }
                                }


                            }
                        }

                    }
                }
            ).classifyStaticImage(imageUri)
        } ?: showToast("No image selected")
    }


    private fun moveToResult(predict: Predict) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.PREDICT, predict)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

}