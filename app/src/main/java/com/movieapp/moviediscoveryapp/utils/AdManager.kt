package com.movieapp.moviediscoveryapp.utils


import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

object AdManager {

    private const val TAG = "AdManager"
    const val INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"
    const val NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110"
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    fun initialize(context: Context, onComplete: () -> Unit = {}) {
        MobileAds.initialize(context) {
            Log.d(TAG, "AdMob SDK initialized")
            onComplete()
        }
    }
    fun loadInterstitialAd(context: Context) {
        if (isInterstitialLoading || interstitialAd != null) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial Ad loaded")
                    interstitialAd = ad
                    isInterstitialLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Interstitial Ad failed: ${error.message}")
                    interstitialAd = null
                    isInterstitialLoading = false
                }
            }
        )
    }
    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial Ad dismissed")
                    interstitialAd = null
                    // Preload next ad
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.e(TAG, "Interstitial Ad show failed: ${error.message}")
                    interstitialAd = null
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial Ad shown")
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "No interstitial ad available, proceeding without ad")
            // Preload for next time
            loadInterstitialAd(activity)
            onAdDismissed()
        }
    }
    @RequiresPermission(Manifest.permission.INTERNET)
    fun loadNativeAd(
        context: Context,
        onAdLoaded: (NativeAd) -> Unit,
        onAdFailed: () -> Unit = {}
    ) {
        val adLoader = AdLoader.Builder(context, NATIVE_AD_ID)
            .forNativeAd { nativeAd ->
                Log.d(TAG, "Native Ad loaded")
                onAdLoaded(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Native Ad failed: ${error.message}")
                    onAdFailed()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun destroyInterstitial() {
        interstitialAd = null
    }
}