package com.example.kmmlib

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

	fun getImagePath(context: Context, uri: Uri?, compressionQuality: Int) : String? {
		kotlin.runCatching {
			val selectedImageUri: Uri? = uri
			val selectedBitmap: Bitmap? = selectedImageUri?.let { uri -> getBitmap(context, uri) }
			val selectedImgFile = createImageTempFile(context)
			selectedBitmap?.let { bitmap ->
				convertBitmapToFile(selectedImgFile, bitmap, compressionQuality)
			}
			return Uri.fromFile(selectedImgFile).path
		}.onFailure {
			it.printStackTrace()
		}
		return null
	}

	// Get Bitmap from URI
	private fun getBitmap(context: Context, imageUri: Uri): Bitmap? {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, imageUri))
		} else {
			context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
				BitmapFactory.decodeStream(inputStream)
			}
		}
	}

	// Convert Bitmap to file
	private fun convertBitmapToFile(destinationFile: File, bitmap: Bitmap, compressionQuality: Int) {
		//create a file to write bitmap data
		destinationFile.createNewFile()
		//Convert bitmap to byte array
		val bos = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, bos)
		val bitmapData = bos.toByteArray()
		//write the bytes in file
		FileOutputStream(destinationFile).apply {
			write(bitmapData)
			flush()
			close()
		}
	}

	// Create Temporary image file
	@Throws(IOException::class)
	fun createImageTempFile(context: Context): File {
		val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
		val imageFileName = "JPEG_" + timeStamp + "_"
		return File.createTempFile(imageFileName, ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
	}
}
