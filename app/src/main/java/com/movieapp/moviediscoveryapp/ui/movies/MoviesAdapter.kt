package com.movieapp.moviediscoveryapp.ui.movies


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.movieapp.moviediscoveryapp.data.local.entity.MovieEntity
import com.movieapp.moviediscoveryapp.databinding.ItemMovieBinding
import com.movieapp.moviediscoveryapp.utils.hide
import com.movieapp.moviediscoveryapp.utils.loadLocalImage
import com.movieapp.moviediscoveryapp.utils.show


class MoviesAdapter(
    private val onItemClick: (MovieEntity) -> Unit,
    private val onDeleteClick: (MovieEntity) -> Unit,
    private val onShareClick: (MovieEntity) -> Unit
) : ListAdapter<MovieEntity, MoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {

    private var isFirstLaunch: Boolean = true

    fun setFirstLaunch(firstLaunch: Boolean) {
        isFirstLaunch = firstLaunch
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieEntity) {
            with(binding) {
                tvMovieName.text = movie.name
                tvMovieGenres.text = movie.genres
                tvMovieRating.text = "⭐ ${movie.rating}"
                tvMovieStatus.text = movie.status
                tvMovieLanguage.text = "🌐 ${movie.language}"
                ivMoviePoster.loadLocalImage(movie.localPosterPath)

                if (isFirstLaunch) {
                    btnDelete.hide()
                    btnShare.hide()
                } else {
                    btnDelete.show()
                    btnShare.show()
                }
                root.setOnClickListener { onItemClick(movie) }

                btnDelete.setOnClickListener {
                    onDeleteClick(movie)
                }

                btnShare.setOnClickListener {
                    onShareClick(movie)
                }
            }
        }
    }
    class MovieDiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
        override fun areItemsTheSame(
            oldItem: MovieEntity,
            newItem: MovieEntity
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: MovieEntity,
            newItem: MovieEntity
        ): Boolean = oldItem == newItem
    }
}