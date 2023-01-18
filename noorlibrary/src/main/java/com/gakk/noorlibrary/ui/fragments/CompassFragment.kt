package com.gakk.noorlibrary.ui.fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.BuildConfig
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentCompassBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.util.MAKKAH_LATITUDE
import com.gakk.noorlibrary.util.MAKKAH_LONGITUDE
import com.gakk.noorlibrary.util.setApplicationLanguage
import java.lang.Math.*
import java.text.DecimalFormat


internal class CompassFragment : Fragment(), SensorEventListener {

    private var mCallback: DetailsCallBack? = null
    private lateinit var binding: FragmentCompassBinding
    private lateinit var mSensorManager: SensorManager
    var userLocation: Array<Double>? = null
    val makkahLocation = arrayOf(MAKKAH_LATITUDE, MAKKAH_LONGITUDE)
    var bearing: Double? = null


    companion object {

        @JvmStatic
        fun newInstance() =
            CompassFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mCallback = requireActivity() as? DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_compass, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle(getString(R.string.cat_compass))

        binding.item = ImageFromOnline("compass_dial_update.png")
        val distance = getDistance(
            AppPreference.getUserCurrentLocation().lat!!,
            AppPreference.getUserCurrentLocation().lng!!,
            MAKKAH_LATITUDE,
            MAKKAH_LONGITUDE
        ).toDouble()
        val distanceOfMakkah =
            context?.resources!!.getString(R.string.makkah_distance) + "  " + DecimalFormat("##.##").format(
                distance / 1000
            ) + "  " + context?.resources!!.getString(R.string.km_text)
        binding.tvDistance.text = distanceOfMakkah

        userLocation = arrayOf(
            AppPreference.getUserCurrentLocation().lat!!,
            AppPreference.getUserCurrentLocation().lng!!
        )
        val d = userLocation?.let { getBearingBetweenTwoPoints(it, makkahLocation) }

        if (d != null) {
            if (d > 0) {
                bearing = d
            } else {
                bearing = 360 + d
            }
        }
    }

    override fun onPause() {
        super.onPause()

        mSensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        val mSensor = mSensorManager.getDefaultSensor(3)
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, 1)
        } else {
            Log.d(BuildConfig.BUILD_TYPE, "Registered for ORIENTATION Sensor")
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        val degree = round(sensorEvent?.values!!.get(0))
        binding.layoutCompass.rotation = -degree.toFloat()

        binding.imgkabba.rotation = bearing?.toFloat()!!

        val degreeTxt =
            degree.toString() + 0x00B0.toChar() + " " + context?.getString(R.string.degree_txt)
        binding.tvLocationCompass.text = degreeTxt

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun getDistance(lat_a: Double, lng_a: Double, lat_b: Double, lng_b: Double): Int {
        var miter = 0
        try {
            val earthRadius = 3958.75
            val latDiff = toRadians(lat_b - lat_a)
            val lngDiff = toRadians(lng_b - lng_a)
            val a = sin(latDiff / 2) * sin(latDiff / 2) +
                    cos(toRadians(lat_a)) * cos(toRadians(lat_b)) *
                    sin(lngDiff / 2) * sin(lngDiff / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val distance = earthRadius * c
            val meterConversion = 1609
            miter = (distance * meterConversion.toFloat().toInt()).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return miter
    }

    fun degreesToRadians(degrees: Double): Double {
        return degrees * (3.1416 / 180.0)
    }

    fun radiansToDegrees(radians: Double): Double {
        return radians * (180.0 / 3.1416)
    }

    fun getBearingBetweenTwoPoints(to: Array<Double>, from: Array<Double>): Double {
        val lat1 = degreesToRadians(degrees = to[0])
        val lon1 = degreesToRadians(degrees = to[1])

        val lat2 = degreesToRadians(degrees = from[0])
        val lon2 = degreesToRadians(degrees = from[1])

        val dLon = lon2 - lon1

        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        val radiansBearing = atan2(y, x)
        return radiansToDegrees(radiansBearing)
    }
}