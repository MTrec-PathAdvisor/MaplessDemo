package com.wherami.demo

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.maps.plugin.locationcomponent.LocationProvider
import wherami.lbs.sdk.adaptive.IALocation
import wherami.lbs.sdk.adaptive.IALocationListener
import wherami.lbs.sdk.adaptive.IALocationManager
import wherami.lbs.sdk.adaptive.IALocationRequest
import wherami.lbs.sdk.adaptive.IAOrientationListener
import wherami.lbs.sdk.adaptive.IAOrientationRequest

class IALocationProvider(context: Context) : LocationProvider, IALocationListener,
    IAOrientationListener {

    private val TAG = "IALocationProvider"

    var context = context
    private val consumers: HashSet<LocationConsumer> = hashSetOf()

    var mIALocationManager: IALocationManager? = null
    private var mLocation: IALocation? = null
    private var latestHeading = 0f

    var started = false;

    init{
        val extra = Bundle()
        extra.putString(IALocationManager.EXTRA_SITENAME, "WKCD_xiqu")
        mIALocationManager = IALocationManager.create(context.applicationContext, extra)
        Log.i(TAG, "onCreate: created WheramiIALocationManager")
    }
    fun startEngine():Boolean{
        Log.i(TAG, "onCreate: reg location update")
        val locUpdate = mIALocationManager?.requestLocationUpdates(IALocationRequest.create(), this)
        val headingUpdate = mIALocationManager?.registerOrientationListener(IAOrientationRequest(5.0, 5.0), this)
        return (locUpdate == true && headingUpdate == true)
    }
    fun stopEngine():Boolean{
        val locUpdate = mIALocationManager?.removeLocationUpdates(this)
        val headingUpdate = mIALocationManager?.unregisterOrientationListener(this)
        mIALocationManager?.destroy()
        return (locUpdate == true && headingUpdate == true)
    }

    override fun registerLocationConsumer(locationConsumer: LocationConsumer) {
        consumers.add(locationConsumer)
        if (!started && consumers.isNotEmpty()){
            startEngine()
        }
    }

    override fun unRegisterLocationConsumer(locationConsumer: LocationConsumer) {
        consumers.remove((locationConsumer))
        if (started && consumers.isEmpty()){
            stopEngine()
        }
    }
    override fun onLocationChanged(iaLocation: IALocation) {
        mLocation = iaLocation
        Handler(context.mainLooper).post {
            consumers.forEach {
                it.onLocationUpdated(Point.fromLngLat(iaLocation.longitude,iaLocation.latitude))
                it.onHorizontalAccuracyRadiusUpdated(iaLocation.accuracy.toDouble())
            }
        }

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
        Log.i(TAG, "onStatusChanged: $provider $status")
    }

    override fun onHeadingChanged(timestamp: Long, heading: Double) {
        Log.i(TAG, "onHeadingChanged: $timestamp $heading")
        latestHeading = heading.toFloat();
        Handler(context.mainLooper).post {
            consumers.forEach {
                it.onBearingUpdated(heading)
            }
        }
    }

    override fun onOrientationChange(timestamp: Long, quaternion: DoubleArray?) {}
}