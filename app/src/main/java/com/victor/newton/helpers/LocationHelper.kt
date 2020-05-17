package com.victor.newton.helpers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.victor.newton.domain.Localitzacio
import java.util.*

class LocationHelper(private val context: Context) {

     fun getCityByLatLong(latitude: Double, Longitude: Double): Localitzacio{
        val geocoder = Geocoder(context, Locale.getDefault())

        val localitzacio = Localitzacio()

        if(Geocoder.isPresent()) {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, Longitude, 1)

            localitzacio.ciutat = addresses[0].locality
            localitzacio.codiPais = addresses[0].countryCode
        }

        return  localitzacio
    }

}