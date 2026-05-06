package com.movieapp.moviediscoveryapp.utils


import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.movieapp.moviediscoveryapp.R
import java.io.File
fun View.show() {
    visibility = View.VISIBLE
}
fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
fun ImageView.loadLocalImage(imagePath: String) {
    if (imagePath.isEmpty()) {
        this.setImageResource(R.drawable.ic_movie_placeholder)
        return
    }
    val file = File(imagePath)
    if (file.exists()) {
        Glide.with(this.context)
            .load(file)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .error(R.drawable.ic_movie_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
            )
            .into(this)
    } else {
        this.setImageResource(R.drawable.ic_movie_placeholder)
    }
}
fun ImageView.loadUrl(url: String) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.ic_movie_placeholder)
                .error(R.drawable.ic_movie_placeholder)
        )
        .into(this)
}