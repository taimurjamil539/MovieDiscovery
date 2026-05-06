package com.movieapp.moviediscoveryapp.ui.movies


import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.nativead.NativeAd
import com.movieapp.moviediscoveryapp.R

import com.movieapp.moviediscoveryapp.data.local.entity.MovieEntity
import com.movieapp.moviediscoveryapp.databinding.FragmentMoviesBinding
import com.movieapp.moviediscoveryapp.utils.AdManager
import com.movieapp.moviediscoveryapp.utils.hide
import com.movieapp.moviediscoveryapp.utils.show
import com.movieapp.moviediscoveryapp.viewmodel.SharedViewModel


class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var moviesAdapter: MoviesAdapter
    private var nativeAd: NativeAd? = null
    private var isFirstLaunch = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupBackPress()
        loadNativeAd()
        viewModel.initializeData()
    }
    private fun setupRecyclerView() {
        moviesAdapter = MoviesAdapter(
            onItemClick = { movie -> handleItemClick(movie) },
            onDeleteClick = { movie -> viewModel.deleteMovie(movie) },
            onShareClick = { movie -> handleShareClick(movie) }
        )
        binding.rvMovies.adapter = moviesAdapter
        binding.rvMovies.setHasFixedSize(true)
    }
    private fun setupObservers() {
        viewModel.isFirstLaunch.observe(viewLifecycleOwner) { first ->
            isFirstLaunch = first
            moviesAdapter.setFirstLaunch(first)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            handleUiState(state)
        }

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            moviesAdapter.submitList(movies)

            if (movies.isEmpty()) {
                binding.tvEmptyState.show()
                binding.rvMovies.hide()
            } else {
                binding.tvEmptyState.hide()
                binding.rvMovies.show()
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
        }
    }
    private fun handleUiState(state: SharedViewModel.UiState) {
        when (state) {
            is SharedViewModel.UiState.Loading -> {
                binding.shimmerMovies.startShimmer()
                binding.shimmerMovies.show()
                binding.rvMovies.hide()
                binding.layoutError.hide()
            }
            is SharedViewModel.UiState.Success -> {
                binding.shimmerMovies.stopShimmer()
                binding.shimmerMovies.hide()
                binding.rvMovies.show()
                binding.layoutError.hide()
            }
            is SharedViewModel.UiState.Error -> {
                binding.shimmerMovies.stopShimmer()
                binding.shimmerMovies.hide()
                binding.rvMovies.hide()
                binding.layoutError.show()
                binding.tvError.text = state.message
                binding.btnRetry.setOnClickListener { viewModel.retry() }
            }
            is SharedViewModel.UiState.Empty -> {
                binding.shimmerMovies.stopShimmer()
                binding.shimmerMovies.hide()
                binding.tvEmptyState.show()
            }
        }
    }
    private fun handleItemClick(movie: MovieEntity) {
        viewModel.setSelectedMovie(movie)
        AdManager.showInterstitialAd(requireActivity()) {
            navigateToDetails()
        }
    }
    private fun handleShareClick(movie: MovieEntity) {
        viewModel.setSelectedMovie(movie)
        AdManager.showInterstitialAd(requireActivity()) {
            navigateToDetails()
        }
    }
    private fun navigateToDetails() {
        if (findNavController().currentDestination?.id == R.id.moviesFragment) {
            findNavController().navigate(
                R.id.action_moviesFragment_to_movieDetailsFragment
            )
        }
    }

    // ─── Back press: Show exit bottom sheet ──────────────────────────
    private fun setupBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitBottomSheet()
                }
            }
        )
    }
    private fun showExitBottomSheet() {
        val bottomSheet = ExitBottomSheetDialog()
        bottomSheet.show(parentFragmentManager, ExitBottomSheetDialog.TAG)
    }
    @RequiresPermission(Manifest.permission.INTERNET)
    private fun loadNativeAd() {
        binding.shimmerNativeMovies.startShimmer()

        AdManager.loadNativeAd(
            context = requireContext(),
            onAdLoaded = { ad ->
                nativeAd = ad
                binding.shimmerNativeMovies.stopShimmer()
                binding.shimmerNativeMovies.hide()
                populateNativeAd(ad)
            },
            onAdFailed = {
                binding.shimmerNativeMovies.stopShimmer()
                binding.shimmerNativeMovies.hide()
                binding.nativeAdContainerMovies.hide()
            }
        )
    }
    private fun populateNativeAd(nativeAd: NativeAd) {
        val nativeAdView = binding.nativeAdViewMovies

        nativeAdView.apply {
            headlineView = binding.adHeadlineMovies
            bodyView = binding.adBodyMovies
            callToActionView = binding.adCallToActionMovies
            iconView = binding.adIconMovies
        }
        binding.adHeadlineMovies.text = nativeAd.headline
        binding.adBodyMovies.text = nativeAd.body
        binding.adCallToActionMovies.text = nativeAd.callToAction
        nativeAd.icon?.let { icon ->
            binding.adIconMovies.setImageDrawable(icon.drawable)
            binding.adIconMovies.show()
        }
        nativeAdView.setNativeAd(nativeAd)
        binding.nativeAdContainerMovies.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nativeAd?.destroy()
        _binding = null
    }
}