package com.pelle.videoreducer.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdView

interface AdManager {
    fun initialize(
        context: Context,
        onInitializationComplete: () -> Unit,
    )

    fun showInterstitial(
        activity: Activity,
        onInterstitialClosed: () -> Unit,
    )

    fun showBanner(bannerAdView: AdView)

    fun isInterstitialLoaded(): Boolean

    fun showAppOpen(activity: Activity)

    fun loadAppOpenAd(context: Context)
}