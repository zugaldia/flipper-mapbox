package com.mapbox.flipper

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.databases.impl.SqliteDatabaseDriver
import com.facebook.flipper.plugins.databases.impl.SqliteDatabaseProvider
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.constants.MapboxConstants
import com.mapbox.mapboxsdk.module.http.HttpRequestUtil
import com.mapbox.mapboxsdk.storage.FileSource
import okhttp3.OkHttpClient
import java.io.File


class PluginApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val networkPair = setupNetwork()
        setupMapbox(networkPair.first)
        setupFlipper(networkPair.second)
    }

    private fun setupNetwork(): Pair<OkHttpClient, NetworkFlipperPlugin> {
        val networkFlipperPlugin = NetworkFlipperPlugin()
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
            .build()

        return Pair(okHttpClient, networkFlipperPlugin)
    }

    private fun setupMapbox(okHttpClient: OkHttpClient) {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        HttpRequestUtil.setOkHttpClient(okHttpClient)
    }

    private fun getMapboxDatabasePath() =
        File(FileSource.getResourcesCachePath(this) + File.separator + "mbgl-offline.db")

    private fun setupFlipper(networkFlipperPlugin: NetworkFlipperPlugin) {
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(networkFlipperPlugin)
            client.addPlugin(
                SharedPreferencesFlipperPlugin(
                    this,
                    MapboxConstants.MAPBOX_SHARED_PREFERENCES
                )
            )
            client.addPlugin(
                DatabasesFlipperPlugin(
                    SqliteDatabaseDriver(
                        this,
                        SqliteDatabaseProvider { listOf(getMapboxDatabasePath()) })
                )
            )
            client.start()
        }
    }

}
