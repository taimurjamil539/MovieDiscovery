package com.movieapp.moviediscoveryapp


import android.app.Application
import com.movieapp.moviediscoveryapp.utils.AdManager

class MovieApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AdManager.initialize(this) {
            AdManager.loadInterstitialAd(this)
        }
    }
}