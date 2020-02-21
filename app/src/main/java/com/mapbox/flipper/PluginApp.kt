package com.mapbox.flipper

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox

class PluginApp: Application() {

    override fun onCreate() {
        super.onCreate()
        setupMapbox()
    }

    private fun setupMapbox() {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
    }
}
