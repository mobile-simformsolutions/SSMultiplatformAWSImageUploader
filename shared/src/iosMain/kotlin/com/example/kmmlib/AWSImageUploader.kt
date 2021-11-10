package com.example.kmmlib

import cocoapods.AWSCore.AWSCognitoCredentialsProvider
import cocoapods.AWSCore.AWSServiceConfiguration
import cocoapods.AWSCore.AWSServiceManager
import cocoapods.AWSS3.*
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.URLByAppendingPathComponent
import platform.darwin.nil
import platform.posix.uint32_t
import kotlin.native.concurrent.freeze

actual class AWSImageUploader actual constructor(private var awsIdentityPoolId: String,
                                                 private var awsBucketName: String,
                                                 private var awsFolderName: String,
                                                 private var awsRegion: AWSRegion){

    // AWS Configuration
    fun addConfiguration(maxRetry: Int?, timeOutIntervalForRequest: Double?, timeoutIntervalForResource: Double?) {
        val configuration = AWSServiceConfiguration(region = awsRegion.region, credentialsProvider = getCredentialsProvider())
        configuration.maxRetryCount = maxRetry?.toUInt() ?: 3.toUInt()
        configuration.timeoutIntervalForRequest = timeOutIntervalForRequest ?: 30.0
        configuration.timeoutIntervalForResource = timeoutIntervalForResource ?: 30.0
        AWSServiceManager.defaultServiceManager()?.defaultServiceConfiguration = configuration
    }

    fun uploadImage(data: NSData, listener: ImageUploadListener, fileName: String, fileType: String) {
        val expression = AWSS3TransferUtilityUploadExpression()
        // Set Progress Update
        setProgressBlock(expression, listener)
        // Start uploading using AWSS3TransferUtility
        val awsTransferUtility = AWSS3TransferUtility.defaultS3TransferUtility()
        awsTransferUtility.uploadData(data, bucket = "$awsBucketName/$awsFolderName", key = fileName, contentType = fileType, expression = expression, completionHandler = getCompletionHandler(fileName, listener).freeze())
    }

    private fun setProgressBlock(expression: AWSS3TransferUtilityUploadExpression, listener: ImageUploadListener) {
        val progressBlock: AWSS3TransferUtilityProgressBlock
        progressBlock = { _, awsProgress ->
            listener.imageUploadProgress((awsProgress?.fractionCompleted?.toFloat() ?: 0f))
        }
        expression.progressBlock = progressBlock
        progressBlock.freeze()
    }

    private fun getCompletionHandler(fileName: String, listener: ImageUploadListener): AWSS3TransferUtilityUploadCompletionHandlerBlock {
        return { _: AWSS3TransferUtilityUploadTask?, error: NSError? ->
            if (error == nil) {
                val url = AWSS3.defaultS3().configuration.endpoint()?.URL()
                val publicURL = url?.URLByAppendingPathComponent("$awsBucketName/$awsFolderName")?.URLByAppendingPathComponent(fileName)
                listener.imageUploadCompleted(publicURL?.absoluteString ?: "")
            } else {
                listener.imageUploadFailure(error?.localizedDescription ?: "")
            }
        }
    }

    private fun getCredentialsProvider(): AWSCognitoCredentialsProvider {
        return AWSCognitoCredentialsProvider(regionType = awsRegion.region, identityPoolId = awsIdentityPoolId)
    }
}
