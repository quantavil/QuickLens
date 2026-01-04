package com.quantavil.quicklens.utils

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

object ImageSearchUploader {
    private const val TAG = "ImageSearchUploader"
    private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
    
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("User-Agent", USER_AGENT)
                .build()
            chain.proceed(request)
        }
        .build()

    private const val IMGBB_API_KEY = "ccc524a226c6a1ff32bca65bff5139d2"

    /**
     * Uploads the bitmap to Catbox as primary, with ImgBB as fallback.
     */
    suspend fun uploadImage(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        val catboxUrl = uploadToCatbox(bitmap)
        if (catboxUrl != null) {
            Log.d(TAG, "Successfully uploaded to Catbox")
            return@withContext catboxUrl
        }
        
        Log.w(TAG, "Catbox failed, falling back to ImgBB")
        val imgbbUrl = uploadToImgBB(bitmap)
        if (imgbbUrl != null) {
            Log.d(TAG, "Successfully uploaded to ImgBB (fallback)")
            return@withContext imgbbUrl
        }
        
        Log.e(TAG, "Both Catbox and ImgBB uploads failed")
        null
    }
    
    private suspend fun uploadToCatbox(bitmap: Bitmap): String? {
        return try {
            val imageBytes = compressBitmap(bitmap)
            
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reqtype", "fileupload")
                .addFormDataPart("fileToUpload", "image.jpg",
                    imageBytes.toRequestBody("image/jpeg".toMediaType()))
                .build()

            val request = Request.Builder()
                .url("https://catbox.moe/user/api.php")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    Log.e(TAG, "Catbox failed: ${response.code}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Catbox upload error", e)
            null
        }
    }

    private suspend fun uploadToImgBB(bitmap: Bitmap): String? {
        if (IMGBB_API_KEY == "YOUR_API_KEY_HERE") {
             Log.e(TAG, "ImgBB API Key is missing.")
             return null
        }

        return try {
            val imageBytes = compressBitmap(bitmap)
            
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", IMGBB_API_KEY)
                .addFormDataPart("image", android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT))
                .build()

            val request = Request.Builder()
                .url("https://api.imgbb.com/1/upload")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    if (jsonResponse != null) {
                         org.json.JSONObject(jsonResponse).getJSONObject("data").getString("url")
                    } else {
                        null
                    }
                } else {
                    Log.e(TAG, "ImgBB failed: ${response.code} ${response.body?.string()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "ImgBB upload error", e)
            null
        }
    }

    private suspend fun compressBitmap(bitmap: Bitmap): ByteArray = withContext(Dispatchers.Default) {
         val resized = ImageUtils.resizeBitmap(bitmap, 1280)
         val outputStream = ByteArrayOutputStream()
         resized.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
         // Recycle intermediate bitmap to prevent memory leak
         if (resized != bitmap) resized.recycle()
         outputStream.toByteArray()
    }
}
