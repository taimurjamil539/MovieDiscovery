package com.movieapp.moviediscoveryapp.ui.splash


import android.Manifest
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.nativead.NativeAd
import com.movieapp.moviediscoveryapp.R
import com.movieapp.moviediscoveryapp.databinding.FragmentSplashBinding
import com.movieapp.moviediscoveryapp.utils.AdManager
import com.movieapp.moviediscoveryapp.utils.hide
import com.movieapp.moviediscoveryapp.utils.show


class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private var countDownTimer: CountDownTimer? = null
    private var nativeAd: NativeAd? = null
    companion object {
        private const val SPLASH_DELAY = 5000L
        private const val TICK_INTERVAL = 1000L
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        loadNativeAd()
        startCountdown()
        AdManager.loadInterstitialAd(requireContext())
    }
    private fun setupUI() {
        // Hide button initially
        binding.btnNext.invisible()
        binding.btnNext.setOnClickListener {
            navigateToMovies()
        }
    }
    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(SPLASH_DELAY, TICK_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000) + 1
                binding.tvCountdown.text = secondsLeft.toString()
                binding.tvCountdown.show()
            }

            override fun onFinish() {
                binding.tvCountdown.hide()
                binding.btnNext.show()
                // Animate button appearance
                binding.btnNext.alpha = 0f
                binding.btnNext.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .start()
            }
        }.start()
    }
    @RequiresPermission(Manifest.permission.INTERNET)
    private fun loadNativeAd() {
        binding.shimmerNativeAd.startShimmer()

        AdManager.loadNativeAd(
            context = requireContext(),
            onAdLoaded = { ad ->
                nativeAd = ad
                binding.shimmerNativeAd.stopShimmer()
                binding.shimmerNativeAd.hide()
                populateNativeAd(ad)
            },
            onAdFailed = {
                binding.shimmerNativeAd.stopShimmer()
                binding.shimmerNativeAd.hide()
                binding.nativeAdContainer.hide()
            }
        )
    }
    private fun populateNativeAd(nativeAd: NativeAd) {
        val nativeAdView = binding.nativeAdView

        nativeAdView.apply {
            headlineView = binding.adHeadline
            bodyView = binding.adBody
            callToActionView = binding.adCallToAction
            iconView = binding.adAppIcon
        }
        binding.adHeadline.text = nativeAd.headline
        binding.adBody.text = nativeAd.body
        binding.adCallToAction.text = nativeAd.callToAction
        nativeAd.icon?.let { icon ->
            binding.adAppIcon.setImageDrawable(icon.drawable)
            binding.adAppIcon.show()
        }
        nativeAdView.setNativeAd(nativeAd)
        binding.nativeAdContainer.show()
    }
    private fun navigateToMovies() {
        findNavController().navigate(R.id.action_splashFragment_to_moviesFragment)
    }
    private fun View.invisible() {
        visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        nativeAd?.destroy()
        _binding = null
    }
}