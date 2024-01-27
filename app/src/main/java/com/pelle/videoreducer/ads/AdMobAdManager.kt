package com.pelle.videoreducer.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


//private const val APP_OPEN_ID = "ca-app-pub-3940256099942544/9257395921"
//private const val INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
private const val APP_OPEN_ID = "ca-app-pub-4404207177366571/9008642199"
private const val INTERSTITIAL_ID = "ca-app-pub-4404207177366571/3060829876"
// private const val BANNER_ID = "ca-app-pub-4404207177366571/3835426314"

class AdMobAdManager: AdManager {

    // region PROPERTIES
    private var interstitialAd: InterstitialAd? = null
    private var appOpenAd: AppOpenAd? = null
    // endregion

    // region OVERRIDDEN METHODS
    override fun initialize(context: Context, onInitializationComplete: () -> Unit) {
        MobileAds.initialize(context) { onInitializationComplete() }
        loadInterstitial(context)
    }

    override fun showInterstitial(activity: Activity, onInterstitialClosed: () -> Unit) {
        interstitialAd?.let {
            setInterstitialCallback(activity, onInterstitialClosed)
            it.show(activity)
        } ?: onInterstitialClosed()
    }

    override fun showBanner(bannerAdView: AdView) {
        bannerAdView.loadAd(AdRequest.Builder().build())
    }

    override fun isInterstitialLoaded(): Boolean = interstitialAd != null

    override fun showAppOpen(activity: Activity) {
        appOpenAd?.let { safeAppOpenAd ->
            safeAppOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    loadAppOpenAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    loadAppOpenAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    // no-op
                }
            }
            safeAppOpenAd.show(activity)
        }
    }

    override fun loadAppOpenAd(context: Context) {
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            APP_OPEN_ID,
            request,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    appOpenAd = null
                }
            },
        )
    }
    // endregion

    // region PRIVATE METHODS
    private fun loadInterstitial(context: Context) {
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@AdMobAdManager.interstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    this@AdMobAdManager.interstitialAd = null
                }
            },
        )
    }

    private fun setInterstitialCallback(context: Context, onInterstitialClosed: () -> Unit) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // no-op
            }

            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadInterstitial(context)
                onInterstitialClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                loadInterstitial(context)
                onInterstitialClosed()
            }

            override fun onAdImpression() {
                // no-op
            }

            override fun onAdShowedFullScreenContent() {
                // no-op
            }
        }
    }
    // endregion

}