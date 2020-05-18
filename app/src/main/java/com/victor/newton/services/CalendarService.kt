//package com.victor.newton.services
//
//
//import android.content.Context
//import android.content.Intent
//import android.os.Handler
//import android.os.Looper
//import android.provider.CalendarContract
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import com.victor.newton.BuildConfig
//import com.victor.newton.R
//import com.victor.newton.domain.ForecastWeather
//import com.victor.newton.domain.OneDayWeather
//import com.victor.newton.helpers.ViewsHelper
//import com.victor.newton.helpers.WeatherIconsHelper
//import okhttp3.*
//import org.json.JSONObject
//import java.io.IOException
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//
//
//class CalendarService(private val context: Context) {
//
//    fun addEventUsingIntent(){
//        val intent = Intent(Intent.ACTION_INSERT)
//            .setData(CalendarContract.Events.CONTENT_URI)
//        context.startActivity(intent)
//    }
//
//}
