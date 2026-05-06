package com.movieapp.moviediscoveryapp.ui.details


import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.nativead.NativeAd
import com.movieapp.moviediscoveryapp.databinding.FragmentMovieDetailsBinding
import com.movieapp.moviediscoveryapp.utils.AdManager
import com.movieapp.moviediscoveryapp.utils.ImageUtils
import com.movieapp.moviediscoveryapp.utils.hide
import com.movieapp.moviediscoveryapp.utils.loadLocalImage
import com.movieapp.moviediscoveryapp.utils.show
import com.movieapp.moviediscoveryapp.viewmodel.SharedViewModel


class MovieDetailsFragment : Fragment() {
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private var nativeAd: NativeAd? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observeSelectedMovie()
        loadNativeAd()
        AdManager.loadInterstitialAd(requireContext())
    }
    private fun setupToolbar() {
        binding.toolbarDetails.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun observeSelectedMovie() {
        viewModel.selectedMovie.observe(viewLifecycleOwner) { movie ->
            movie?.let {
                // Populate fields
                binding.tvMovieName.text = it.name
                binding.tvDescription.text = it.summary
                binding.tvGenres.text = "Genres: ${it.genres}"
                binding.tvStatus.text = "Status: ${it.status}"
                binding.tvPremiered.text = "Premiered: ${it.premiered}"
                binding.tvLanguage.text = "Language: ${it.language}"
                binding.tvRating.text = "Rating: ${it.rating}/10"
                binding.ivMoviePoster.loadLocalImage(it.localPosterPath)
                binding.btnSharePoster.setOnClickListener { _ ->
                    ImageUtils.shareMovie(
                        context = requireContext(),
                        movieName = it.name,
                        imagePath = it.localPosterPath
                    )
                }
            }
        }
    }
    @RequiresPermission(Manifest.permission.INTERNET)
    private fun loadNativeAd() {
        binding.shimmerNativeDetails.startShimmer()

        AdManager.loadNativeAd(
            context = requireContext(),
            onAdLoaded = { ad ->
                nativeAd = ad
                binding.shimmerNativeDetails.stopShimmer()
                binding.shimmerNativeDetails.hide()
                populateNativeAd(ad)
            },
            onAdFailed = {
                binding.shimmerNativeDetails.stopShimmer()
                binding.shimmerNativeDetails.hide()
                binding.nativeAdContainerDetails.hide()
            }
        )
    }
    private fun populateNativeAd(nativeAd: NativeAd) {
        val nativeAdView = binding.nativeAdViewDetails

        nativeAdView.apply {
            headlineView = binding.adHeadlineDetails
            bodyView = binding.adBodyDetails
            callToActionView = binding.adCallToActionDetails
            iconView = binding.adIconDetails
        }

        binding.adHeadlineDetails.text = nativeAd.headline
        binding.adBodyDetails.text = nativeAd.body
        binding.adCallToActionDetails.text = nativeAd.callToAction

        nativeAd.icon?.let { icon ->
            binding.adIconDetails.setImageDrawable(icon.drawable)
            binding.adIconDetails.show()
        }
        nativeAdView.setNativeAd(nativeAd)
        binding.nativeAdContainerDetails.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        nativeAd?.destroy()
        _binding = null
    }
}