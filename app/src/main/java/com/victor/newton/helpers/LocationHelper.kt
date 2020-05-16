package com.victor.newton.helpers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.*

class LocationHelper(private val context: Context) {

     fun getCityByLatLong(latitude: Double, Longitude: Double): String{
        val geocoder = Geocoder(context, Locale.getDefault())

        var cityName = ""
        var countryCode = ""

        if(Geocoder.isPresent()) {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, Longitude, 1)
            cityName = addresses[0].locality
            countryCode = addresses[0].countryCode
        }

        return "$cityName, $countryCode"
    }

}