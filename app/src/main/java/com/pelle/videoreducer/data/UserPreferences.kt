package com.pelle.videoreducer.data

import android.content.Context
import android.content.SharedPreferences

private const val PREFERENCES_NAME = "preferences"
private const val HAS_FINISHED_ONBOARDING_KEY = "HAS_FINISHED_ONBOARDING_KEY"

class UserPreferences(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var hasFinishedOnBoarding: Boolean
        get() = preferences.getBoolean(HAS_FINISHED_ONBOARDING_KEY, false)
        set(value) = preferences.edit().putBoolean(HAS_FINISHED_ONBOARDING_KEY, value).apply()

}