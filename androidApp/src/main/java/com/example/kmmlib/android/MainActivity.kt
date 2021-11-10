package com.example.kmmlib.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.kmmlib.AWSImageUploader
import com.example.kmmlib.AWSRegion
import com.example.kmmlib.FileUtils
import com.example.kmmlib.FileUtils.getImagePath
import com.example.kmmlib.ImageUploadListener
import java.io.File

class MainActivity : AppCompatActivity(),  View.OnClickListener {

    private var captureImgUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCamera -> {
                kotlin.runCatching {
                    val capturedImgFile = FileUtils.createImageTempFile(this)
                    captureImgUrl = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", capturedImgFile)
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, captureImgUrl)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    launchCameraActivity.launch(cameraIntent)
                }.onFailure { throwable ->
                    throwable.printStackTrace()
                }
            }
        }
    }

    private var launchCameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            uploadImageInAws(File(getImagePath(this, captureImgUrl, 50) ?: ""))
        }
    }

    private fun uploadImageInAws(file: File?) {
        val identityPoolId = "us-east-1:c42ab2f1-5f5c-406d-8c75-5ffb25a697f1"
        val bucketName = "golfpoker-demo"
        val folderName = "Folder"
        val awsUploader = AWSImageUploader(identityPoolId, bucketName, folderName, AWSRegion.US_EAST_1)
        awsUploader.addClientConfiguration()
        awsUploader.uploadImage(file = file,
            context = this,
            listener = object : ImageUploadListener {
                override fun imageUploadProgress(percentage: Float) {
                    findViewById<TextView>(R.id.txtProgress).text = "Progress :- $percentage%"
                }

                // On Image Uploaded Success
                override fun imageUploadCompleted(url: String) {
                    Log.d("Tag", "Upload complete: $url")
                    findViewById<TextView>(R.id.txtProgress).text = "Image Uploaded successfully"
                }

                // On Image Uploaded Failure
                override fun imageUploadFailure(message: String) {
                    Log.d("Tag", "Failure: $message")
                }
            },
            fileName = "test2.jpeg"
        )
    }
}
