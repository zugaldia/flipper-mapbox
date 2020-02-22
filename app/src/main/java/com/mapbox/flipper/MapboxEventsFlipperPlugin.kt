package com.mapbox.flipper

import android.content.Context
import android.util.Log
import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.storage.FileSource
import java.io.File
import java.util.*

class MapboxEventsFlipperPlugin : FlipperPlugin,
    MapView.OnWillStartLoadingMapListener,
    MapView.OnDidFinishLoadingMapListener {

    companion object {
        const val LOG_TAG = "MapboxFlipperPlugin"

        fun getDatabaseFiles(context: Context): List<File> =
            listOf(File(FileSource.getResourcesCachePath(context) + File.separator + "mbgl-offline.db"))
    }

    private var connection: FlipperConnection? = null

    override fun getId(): String = "flipper-mapbox-events"

    override fun runInBackground(): Boolean = false

    override fun onConnect(connection: FlipperConnection?) {
        Log.d(LOG_TAG, "Connected.")
        this.connection = connection

        sendMessage("connected", Mapbox.isConnected().toString())
        sendMessage("SKU token", Mapbox.getSkuToken())
        sendMessage("access token", Mapbox.getAccessToken() ?: "(empty)")
    }

    override fun onDisconnect() {
        Log.d(LOG_TAG, "Disconnected.")
        this.connection = null
    }

    fun sendMessage(title: String, value: String) {
        connection?.send(
            "newRow", FlipperObject.Builder()
                .put("id", UUID.randomUUID().toString())
                .put("title", title)
                .put("value", value).build()
        )
    }

    override fun onWillStartLoadingMap() {
        sendMessage("WillStartLoadingMap", System.currentTimeMillis().toString())
    }

    override fun onDidFinishLoadingMap() {
        sendMessage("DidFinishLoadingMap", System.currentTimeMillis().toString())
    }
}
