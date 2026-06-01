package com.example.myapplication.data.remote

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

object CloudinaryManager {
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        
        val config = mapOf(
            "cloud_name" to "dnsktunfa",
            "api_key" to "768551423838326",
            "api_secret" to "sBmxMMl9_T98jJhBIAexqsjK-gU"
        )
        MediaManager.init(context, config)
        isInitialized = true
    }

    fun uploadPhoto(uri: Uri, folder: String, onResult: (String?) -> Unit) {
        MediaManager.get().upload(uri)
            .option("folder", folder)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    onResult(url)
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    onResult(null)
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
    }
}
