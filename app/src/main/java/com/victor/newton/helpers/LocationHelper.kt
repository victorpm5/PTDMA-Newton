package com.victor.newton.helpers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import com.victor.newton.domain.Localitzacio
import java.util.*

class LocationHelper(private val context: Context) {

     fun getCityByLatLong(latitude: Double, Longitude: Double): Localitzacio{
        val geocoder = Geocoder(context, Locale.getDefault())

        val localitzacio = Localitzacio()

        if(Geocoder.isPresent()) {

            try {
                val addresses: List<Address> = geocoder.getFromLocation(latitude, Longitude, 1)

                localitzacio.ciutat = addresses[0].locality
                localitzacio.codiPais = addresses[0].countryCode
            } catch(e: Exception){
                println("Error: " + e.message)
                //Si geocoder no funciona posem com a default location bcn
                localitzacio.ciutat = "barcelona"
                Toast.makeText(context, "Fail retrieving city from location. Barcelona has been set as default value", Toast.LENGTH_SHORT).show()
            }
        }

        return  localitzacio
    }

}