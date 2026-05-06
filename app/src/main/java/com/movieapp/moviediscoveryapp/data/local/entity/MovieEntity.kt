package com.movieapp.moviediscoveryapp.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "summary")
    val summary: String,

    @ColumnInfo(name = "poster_url")
    val posterUrl: String,

    @ColumnInfo(name = "local_poster_path")
    val localPosterPath: String,

    @ColumnInfo(name = "rating")
    val rating: Double,

    @ColumnInfo(name = "genres")
    val genres: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "premiered")
    val premiered: String,

    @ColumnInfo(name = "language")
    val language: String
)