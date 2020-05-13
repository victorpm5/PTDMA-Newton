//package com.victor.newton.services
//
//
//import android.view.View
//import com.victor.newton.BuildConfig
//import okhttp3.*
//import java.io.IOException
//
//
//class WeatherService {
//
//    private val apiKey: String = BuildConfig.WeatherApiKey
//    private val weatherURL: String = "https://api.openweathermap.org/data/2.5/weather"
//    private val forecastURL: String = "https://api.openweathermap.org/data/2.5/forecast"
//
//
//    fun getCurrentWeatherByLocation(latitude: Double, longitude: Double, view: View){
//
//        val url = HttpUrl.parse(weatherURL)!!.newBuilder()
//            .addQueryParameter("lat", latitude.toString())
//            .addQueryParameter("lon", longitude.toString())
//            .addQueryParameter("APPID", apiKey)
//            .build()
//
//        val request = Request.Builder().url(url).build()
//
////        val response = OkHttpClient().newCall(request).execute()
//
//        OkHttpClient().newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    e.printStackTrace()
//                }
//                override fun onResponse(call: Call, response: Response) {
//
////                    val jsonData = response?.body()?.string()
////                    val jsonObject  = JSONObject(jsonData)
////
////                    val location: TextView = view.findViewById<TextView>(R.id.location)
////                    val data: TextView = view.findViewById<TextView>(R.id.date)
////                    val temperatura: TextView = view.findViewById<TextView>(R.id.temperature)
////                    val minTemp: TextView = view.findViewById<TextView>(R.id.tempMin)
////                    val maxTemp: TextView = view.findViewById<TextView>(R.id.maxTemp)
////                    val description: TextView = view.findViewById<TextView>(R.id.description)
////                    val humidity: TextView = view.findViewById<TextView>(R.id.humidity)
////
////                    location.text = jsonObject.getString("name")
////                    data.text = jsonObject.getInt("dt").toString()
////                    temperatura.text = "${jsonObject.getJSONObject("main").getInt("temp")} ºC"
////                    minTemp.text = "${jsonObject.getJSONObject("main").getInt("temp_min")} ºC"
////                    maxTemp.text = "${jsonObject.getJSONObject("main").getInt("temp_max")} ºC"
////                    description.text = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
////                    humidity.text = "${jsonObject.getJSONObject("main").getInt("humidity")} %"
//                }
//            })
//
//
//    }
//
//}
