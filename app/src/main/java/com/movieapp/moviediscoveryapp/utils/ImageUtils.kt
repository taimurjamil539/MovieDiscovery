package com.movieapp.moviediscoveryapp.utils


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ImageUtils {

    fun shareMovie(context: Context, movieName: String, imagePath: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_SUBJECT, "Check out this movie: $movieName")
                putExtra(Intent.EXTRA_TEXT, "I found this amazing movie: $movieName")

                if (imagePath.isNotEmpty()) {
                    val imageFile = File(imagePath)
                    if (imageFile.exists()) {
                        val imageUri: Uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            imageFile
                        )
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
            }
            context.startActivity(
                Intent.createChooser(shareIntent, "Share $movieName via")
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    fun isImageAvailable(imagePath: String): Boolean {
        if (imagePath.isEmpty()) return false
        return File(imagePath).exists()
    }
    fun getFileUri(context: Context, imagePath: String): Uri? {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}