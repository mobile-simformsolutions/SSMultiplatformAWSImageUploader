package com.example.kmmlib

expect class AWSImageUploader(awsIdentityPoolId: String, awsBucketName: String, awsFolderName: String, awsRegion: AWSRegion)

/**
 * Image upload listener
 */
interface ImageUploadListener {
    /** upload image progress */
    fun imageUploadProgress(percentage: Float)

    /** upload image completed */
    fun imageUploadCompleted(url: String)

    /** upload image failure */
    fun imageUploadFailure(message: String)
}