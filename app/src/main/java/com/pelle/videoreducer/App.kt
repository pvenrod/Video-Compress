package com.pelle.videoreducer

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.pelle.videoreducer.ads.AdManager
import com.pelle.videoreducer.modules.appModule
import com.pelle.videoreducer.modules.viewModelModule
import com.pelle.videoreducer.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

private const val ONESIGNAL_APP_ID = "c272344a-8c55-4501-b906-ec05816d9dfa"

class App: Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private val adManager: AdManager by inject()
    private var currentActivity: Activity? = null
    var comesFromFilePicker = false

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(viewModelModule + appModule)
        }
        adManager.apply {
            initialize(this@App) { /* no-op */ }
            loadAppOpenAd(this@App)
        }
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        initOneSignal()
    }

    private fun initOneSignal() {
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                startActivity(Intent(this@App, MainActivity::class.java))
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onMoveToForeground() {
        currentActivity?.let { act ->
            if (!comesFromFilePicker) {
                adManager.showAppOpen(act)
            } else {
                comesFromFilePicker = false
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // no-op
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        // no-op
    }

    override fun onActivityPaused(activity: Activity) {
        // no-op
    }

    override fun onActivityStopped(activity: Activity) {
        // no-op
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        // no-op
    }
}