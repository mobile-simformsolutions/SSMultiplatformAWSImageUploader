package com.example.kmmlib

import android.content.Context
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.S3ClientOptions
import com.amazonaws.services.s3.model.CannedAccessControlList
import java.io.File

actual class AWSImageUploader actual constructor(private var awsIdentityPoolId: String,
                                                 private var awsBucketName: String,
                                                 private var awsFolderName: String,
                                                 private var awsRegion: AWSRegion) {

    private var clientConfiguration = ClientConfiguration()

    fun addClientConfiguration(maxRetry: Int = 3, connectionTimeOut: Int = 5 * 1000, socketTimeout: Int = 5 * 1000) {
         clientConfiguration.apply {
            this.maxErrorRetry = maxRetry
            this.connectionTimeout = connectionTimeOut
            this.socketTimeout = socketTimeout
        }
    }

    fun uploadImage(file: File?, context: Context, listener: ImageUploadListener, fileName: String) {
            kotlin.runCatching {
                val amazonClient = getAmazonS3Client(context = context, identityPoolId = awsIdentityPoolId, region = awsRegion.region)
                val amazonTransfer = getTransferUtility(context = context, amazonS3Client = amazonClient, bucket = awsBucketName)
                if (file?.length() ?: 0 > 0) {
                    val profilePoolBucket = "$awsBucketName/$awsFolderName"
                    var previousId = 0
                    val observer = amazonTransfer.upload(profilePoolBucket, fileName, file, CannedAccessControlList.PublicReadWrite)
                    observer?.setTransferListener(object: TransferListener {
                        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                            val percentage: Int
                            if (bytesTotal != 0L) {
                                percentage = (bytesCurrent * 100 / bytesTotal).toInt()
                                if (percentage == 100 && previousId != id) {
                                    amazonClient.getResourceUrl(profilePoolBucket, fileName)?.let {
                                        listener.imageUploadCompleted(it)
                                        file?.delete()
                                    }
                                    previousId = id
                                } else if (percentage != 100) {
                                    listener.imageUploadProgress(percentage.toFloat())
                                }
                            }
                        }

                        override fun onStateChanged(id: Int, state: TransferState?) {
                            // On State changed
                        }

                        override fun onError(id: Int, ex: Exception?) {
                            listener.imageUploadFailure(ex?.message ?: "")
                        }
                    })
                } else {
                    listener.imageUploadFailure("Error uploading file. File size cannot be less then 0")
                }
            }.onFailure { exception ->
                listener.imageUploadFailure(exception.message ?: "")
            }
    }

    private fun getAmazonS3Client(context: Context, identityPoolId: String, region: Regions): AmazonS3Client {
        val cognitoCachingCredentialsProvider = CognitoCachingCredentialsProvider(context, identityPoolId, region)
        val builder = S3ClientOptions.builder().setPathStyleAccess(true).build()
        val s3Client = AmazonS3Client(cognitoCachingCredentialsProvider, Region.getRegion(region), clientConfiguration)
        s3Client.setS3ClientOptions(builder)
        return s3Client
    }

    private fun getTransferUtility(context: Context, bucket: String, amazonS3Client: AmazonS3Client): TransferUtility {
        return TransferUtility.builder()
            .context(context)
            .s3Client(amazonS3Client)
            .defaultBucket(bucket)
            .build()
    }
}
