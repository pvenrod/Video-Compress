package com.pelle.videoreducer.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import com.pelle.videoreducer.ads.AdManager
import com.pelle.videoreducer.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class MainActivity : FragmentActivity(), MainActivityApi {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun showStatusBar() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            view.onApplyWindowInsets(windowInsets)
        }
    }

    override fun hideStatusBar() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            view.onApplyWindowInsets(windowInsets)
        }
    }

}

interface MainActivityApi {
    fun showStatusBar()
    fun hideStatusBar()
}