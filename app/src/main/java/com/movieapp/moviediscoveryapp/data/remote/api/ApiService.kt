package com.movieapp.moviediscoveryapp.data.remote.api


import com.movieapp.moviediscoveryapp.data.remote.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("shows")
    suspend fun getShows(): Response<List<MovieResponse>>
}