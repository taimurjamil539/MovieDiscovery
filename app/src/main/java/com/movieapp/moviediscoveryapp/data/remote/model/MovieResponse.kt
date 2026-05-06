package com.movieapp.moviediscoveryapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String?,

    @SerializedName("summary")
    val summary: String?,

    @SerializedName("image")
    val image: ImageResponse?,

    @SerializedName("rating")
    val rating: RatingResponse?,

    @SerializedName("genres")
    val genres: List<String>?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("premiered")
    val premiered: String?,

    @SerializedName("language")
    val language: String?
)

data class ImageResponse(
    @SerializedName("medium")
    val medium: String?,

    @SerializedName("original")
    val original: String?
)

data class RatingResponse(
    @SerializedName("average")
    val average: Double?
)