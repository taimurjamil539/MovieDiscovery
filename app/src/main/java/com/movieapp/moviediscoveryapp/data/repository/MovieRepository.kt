package com.movieapp.moviediscoveryapp.data.repository


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.movieapp.moviediscoveryapp.data.local.dao.MovieDao
import com.movieapp.moviediscoveryapp.data.local.entity.MovieEntity
import com.movieapp.moviediscoveryapp.data.remote.api.ApiService
import com.movieapp.moviediscoveryapp.data.remote.model.MovieResponse
import com.movieapp.moviediscoveryapp.utils.Results

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class MovieRepository(
    private val apiService: ApiService,
    private val movieDao: MovieDao,
    private val context: Context
) {
    companion object {
        private const val TAG = "MovieRepository"
        private const val MAX_MOVIES = 20
        private const val IMAGES_DIR = "movie_posters"
    }
    suspend fun isDataAvailableLocally(): Boolean {
        return withContext(Dispatchers.IO) {
            movieDao.getMovieCount() > 0
        }
    }
    suspend fun fetchAndSaveMovies(): Results<List<MovieEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getShows()

                if (response.isSuccessful) {
                    val movieList = response.body()
                        ?.take(MAX_MOVIES)
                        ?: emptyList()

                    val entities = movieList.mapNotNull { movie ->
                        convertToEntityWithImageDownload(movie)
                    }

                    if (entities.isNotEmpty()) {
                        movieDao.insertMovies(entities)
                    }

                    Results.Success(entities)
                } else {
                    Results.Error("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchAndSaveMovies error: ${e.message}", e)
                Results.Error(e.message ?: "Unknown error occurred", e)
            }
        }
    }
    suspend fun getLocalMovies(): List<MovieEntity> {
        return withContext(Dispatchers.IO) {
            movieDao.getAllMoviesSuspend()
        }
    }

    fun getLocalMoviesLive() = movieDao.getAllMovies()
    suspend fun deleteMovie(movie: MovieEntity): Results<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val imageFile = File(movie.localPosterPath)
                if (imageFile.exists()) {
                    imageFile.delete()
                    Log.d(TAG, "Image deleted: ${movie.localPosterPath}")
                }
                movieDao.deleteMovieById(movie.id)
                Results.Success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "deleteMovie error: ${e.message}", e)
                Results.Error(e.message ?: "Delete failed", e)
            }
        }
    }

    private suspend fun convertToEntityWithImageDownload(
        movie: MovieResponse
    ): MovieEntity? {
        return try {
            val imageUrl = movie.image?.original
                ?: movie.image?.medium
                ?: ""

            val localPath = if (imageUrl.isNotEmpty()) {
                downloadAndSaveImage(imageUrl, movie.id)
            } else {
                ""
            }

            MovieEntity(
                id = movie.id,
                name = movie.name ?: "Unknown",
                summary = cleanHtmlTags(movie.summary ?: "No description available."),
                posterUrl = imageUrl,
                localPosterPath = localPath,
                rating = movie.rating?.average ?: 0.0,
                genres = movie.genres?.joinToString(", ") ?: "N/A",
                status = movie.status ?: "Unknown",
                premiered = movie.premiered ?: "N/A",
                language = movie.language ?: "N/A"
            )
        } catch (e: Exception) {
            Log.e(TAG, "convertToEntity error for movie ${movie.id}: ${e.message}")
            null
        }
    }
    private fun downloadAndSaveImage(imageUrl: String, movieId: Int): String {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(imageUrl).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                    ?: return ""

                val bitmap = BitmapFactory.decodeStream(inputStream)
                    ?: return ""

                saveBitmapToStorage(bitmap, movieId)
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "Image download error: ${e.message}")
            ""
        }
    }
    private fun saveBitmapToStorage(bitmap: Bitmap, movieId: Int): String {
        return try {
            val directory = File(context.filesDir, IMAGES_DIR)
            if (!directory.exists()) directory.mkdirs()

            val file = File(directory, "movie_$movieId.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            Log.d(TAG, "Image saved: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "saveBitmap error: ${e.message}")
            ""
        }
    }
    private fun cleanHtmlTags(html: String): String {
        return html.replace(Regex("<[^>]*>"), "").trim()
    }
}