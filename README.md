# Flipper + Mapbox

Mapbox integration and sample plugin for [Facebook Flipper](https://fbflipper.com).

This project shows how to configure Flipper with the
[Mapbox Maps SDK for Android](https://docs.mapbox.com/android/maps/overview),
and contains a sample plugin that sends map events to Flipper.

## NetworkFlipperPlugin

Instantiate the plugin:

```
val networkFlipperPlugin = NetworkFlipperPlugin()
```

Create a new `OkHttp` object and add `NetworkFlipperPlugin` as an interceptor:

```
val okHttpClient = OkHttpClient.Builder()
    .addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
    .build()
```

Finally, pass this `OkHttp` client to Mapbox and enable the plugin:

```
HttpRequestUtil.setOkHttpClient(okHttpClient)
client.addPlugin(networkFlipperPlugin)
```

## SharedPreferencesFlipperPlugin

You can follow Mapbox-specific preferences key/values by setting up `SharedPreferencesFlipperPlugin`
with the Mapbox preferences filename (`MapboxConstants` is provided by the Maps SDK):

```
client.addPlugin(
    SharedPreferencesFlipperPlugin(this, MapboxConstants.MAPBOX_SHARED_PREFERENCES)
)
```

## DatabasesFlipperPlugin

The Maps SDK stores the style resources in a local SQLite database. You can browse the content
of this database by using the `MapboxEventsFlipperPlugin.getDatabaseFiles()` utility method:

```
client.addPlugin(
    DatabasesFlipperPlugin(
        SqliteDatabaseDriver(this,
            SqliteDatabaseProvider { MapboxEventsFlipperPlugin.getDatabaseFiles(this) })
    )
)

```

## Mapbox Events Plugin

This repo contains a new and experimental plugin that sends map events to Flipper. It
currently supports:
- Connectivity status
- Access token
- Map events: `onWillStartLoadingMap` and `onDidFinishLoadingMap`.

Installing it is a two-step process. First, register plugin (this example uses Koin for
dependency injection in the `Application` class):

```
val flipperPlugin: MapboxEventsFlipperPlugin = get()
client.addPlugin(flipperPlugin)
```

Then, in the `Fragment` or `Activity` where you instantiate the map, install the listeners:

```
private val flipperPlugin: MapboxEventsFlipperPlugin by inject()

...

mapView?.addOnWillStartLoadingMapListener(flipperPlugin)
mapView?.addOnDidFinishLoadingMapListener(flipperPlugin)
```
