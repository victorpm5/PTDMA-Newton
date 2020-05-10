package com.victor.newton.helpers

import com.victor.newton.R

class WeatherIconsHelper {

    fun getImageByIconID(iconId: String): Int{
        when (iconId) {
            "01d" -> return R.drawable.sun
            "01n" -> return R.drawable.night
            "02d" -> return R.drawable.partly_cloudy_day
            "02n" -> return R.drawable.partly_cloudy_night
            "03d" -> return R.drawable.partly_cloudy_day
            "03n" -> return R.drawable.partly_cloudy_night
            "04d" -> return R.drawable.partly_cloudy_day
            "04n" -> return R.drawable.partly_cloudy_night
            "09d" -> return R.drawable.rain
            "09n" -> return R.drawable.rainy_night
            "10d" -> return R.drawable.rain
            "10n" -> return R.drawable.rainy_night
            "11d" -> return R.drawable.storm
            "11n" -> return R.drawable.storm_night
            "13d" -> return R.drawable.snow
            "13n" -> return R.drawable.snow
            "50d" -> return R.drawable.fog
            "50n" -> return R.drawable.fog
            else -> return R.drawable.fog
        }


    }


}