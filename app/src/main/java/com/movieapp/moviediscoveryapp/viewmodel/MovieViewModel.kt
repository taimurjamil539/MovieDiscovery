package com.movieapp.moviediscoveryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.movieapp.moviediscoveryapp.data.local.database.AppDatabase
import com.movieapp.moviediscoveryapp.data.local.entity.MovieEntity


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.movieapp.moviediscoveryapp.data.remote.network.NetworkModule
import com.movieapp.moviediscoveryapp.data.repository.MovieRepository
import com.movieapp.moviediscoveryapp.utils.Results
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository
    private val _movies = MutableLiveData<List<MovieEntity>>()
    val movies: LiveData<List<MovieEntity>> = _movies
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState
    private val _isFirstLaunch = MutableLiveData<Boolean>()
    val isFirstLaunch: LiveData<Boolean> = _isFirstLaunch
    private val _selectedMovie = MutableLiveData<MovieEntity?>()
    val selectedMovie: LiveData<MovieEntity?> = _selectedMovie
    private val _deleteResult = MutableLiveData<Results<Unit>>()
    val deleteResult: LiveData<Results<Unit>> = _deleteResult
    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
        object Empty : UiState()
    }

    init {
        val database = AppDatabase.getInstance(application)
        repository = MovieRepository(
            apiService = NetworkModule.apiService,
            movieDao = database.movieDao(),
            context = application
        )
    }
    fun initializeData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val isLocalAvailable = repository.isDataAvailableLocally()
            _isFirstLaunch.value = !isLocalAvailable

            if (isLocalAvailable) {
                loadLocalMovies()
            } else {
                fetchMoviesFromApi()
            }
        }
    }
    private fun fetchMoviesFromApi() {
        viewModelScope.launch {
            when (val result = repository.fetchAndSaveMovies()) {
                is Results.Success -> {
                    _movies.value = result.data
                    _uiState.value = if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success
                    }
                }
                is Results.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is Results.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }
    private fun loadLocalMovies() {
        viewModelScope.launch {
            val localMovies = repository.getLocalMovies()
            _movies.value = localMovies
            _uiState.value = if (localMovies.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success
            }
        }
    }
    fun setSelectedMovie(movie: MovieEntity) {
        _selectedMovie.value = movie
    }
    fun deleteMovie(movie: MovieEntity) {
        viewModelScope.launch {
            val result = repository.deleteMovie(movie)
            _deleteResult.value = result

            if (result is Results.Success) {
                // Update current list
                val currentList = _movies.value?.toMutableList() ?: mutableListOf()
                currentList.remove(movie)
                _movies.value = currentList
            }
        }
    }
    fun retry() {
        fetchMoviesFromApi()
    }
}
