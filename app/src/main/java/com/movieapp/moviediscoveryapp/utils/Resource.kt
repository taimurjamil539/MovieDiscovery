package com.movieapp.moviediscoveryapp.utils



sealed class Results<out T> {
    data class Success<out T>(val data: T) : Results<T>()
    data class Error(val message: String, val exception: Exception? = null) : Results<Nothing>()
    object Loading : Results<Nothing>()
}



