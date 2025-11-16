package com.hornswoggled

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HornswoggledApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any SDKs or libraries here
    }
}
