package com.pelle.videoreducer.modules

import com.google.firebase.analytics.FirebaseAnalytics
import com.pelle.videoreducer.ads.AdManager
import com.pelle.videoreducer.ads.AdMobAdManager
import com.pelle.videoreducer.data.UserPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

var appModule = module {
    single<UserPreferences> {
        UserPreferences(
            context = androidContext(),
        )
    }

    single<AdManager> {
        AdMobAdManager()
    }

    single<FirebaseAnalytics> {
        FirebaseAnalytics.getInstance(androidContext())
    }
}