package com.victor.newton.services


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.victor.newton.BuildConfig
import com.victor.newton.R
import com.victor.newton.helpers.ViewsHelper
import com.victor.newton.helpers.WeatherIconsHelper
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class WeatherService(private val context: Context) {

    private val apiKey: String = BuildConfig.WeatherApiKey
    private val weatherURL: String = "https://api.openweathermap.org/data/2.5/weather"
    private val forecastURL: String = "https://api.openweathermap.org/data/2.5/forecast"


    fun getCurrentWeatherByLocation(latitude: Double?, longitude: Double?, city: String, view: View) {

        val url = generaUrl(weatherURL,latitude,longitude,city,"metric")

        val request = Request.Builder().url(url).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                //TODO mostra missatge error al usuari
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body()?.string()
                val jsonObject = JSONObject(jsonData)

                actualitzaVistaWeather(view, jsonObject)
            }
        })
    }

    fun getForecastWeatherByLocation(latitude: Double?, longitude: Double?, city:String, view: View) {

        val url = generaUrl(forecastURL,latitude,longitude,city,"metric")

        val request = Request.Builder().url(url).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                //TODO mostra missatge error al usuari
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body()?.string()
                val jsonObject = JSONObject(jsonData)

                actualitzaVistaForecast(view, jsonObject)
            }
        })
    }

    private fun generaUrl(base: String,latitude: Double?, longitude: Double?, city:String, units:String ) :HttpUrl{

        val url = HttpUrl.parse(base)!!.newBuilder()
            .addQueryParameter("APPID", apiKey)
            .addQueryParameter("units", units)

        if(city.trim() != ""){
            url.addQueryParameter("q", city)
        }else {
            url.addQueryParameter("lat", latitude.toString())
                .addQueryParameter("lon", longitude.toString())
        }

        return url.build()
    }



    private fun actualitzaVistaWeather(view: View, jsonObject: JSONObject){

        val handler = Handler(Looper.getMainLooper())

        handler.post {
            val location: TextView = view.findViewById(R.id.location)
            val data: TextView = view.findViewById(R.id.date)
            val temperatura: TextView = view.findViewById(R.id.temperature)
            val minTemp: TextView = view.findViewById(R.id.tempMin)
            val maxTemp: TextView = view.findViewById(R.id.maxTemp)
            val description: TextView = view.findViewById(R.id.description)
            val humidity: TextView = view.findViewById(R.id.humidity)
            val weatherImage: ImageView = view.findViewById(R.id.weatherImage)

            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")

            data.text = LocalDate.now().format(dateFormatter)
            location.text = jsonObject.getString("name")
            temperatura.text = "${jsonObject.getJSONObject("main").getInt("temp")}ºC"
            minTemp.text = "${jsonObject.getJSONObject("main").getInt("temp_min")}ºC"
            maxTemp.text = "${jsonObject.getJSONObject("main").getInt("temp_max")}ºC"
            description.text =
                jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
            humidity.text = "${jsonObject.getJSONObject("main").getInt("humidity")}% Hum."
            weatherImage.setImageResource(
                WeatherIconsHelper().getImageByIconID(
                    jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon")))

            ViewsHelper(context).showView(view)
        }

    }

    private fun actualitzaVistaForecast(view: View, jsonObject: JSONObject) {

        val handler = Handler(Looper.getMainLooper())

        handler.post {
            val location: TextView = view.findViewById(R.id.location)
            val data: TextView = view.findViewById(R.id.date)
            val data2: TextView = view.findViewById(R.id.date2)
            val data3: TextView = view.findViewById(R.id.date3)
            val weatherImage: ImageView = view.findViewById(R.id.weatherImage)
            val weatherImage2: ImageView = view.findViewById(R.id.weatherImage2)
            val weatherImage3: ImageView = view.findViewById(R.id.weatherImage3)
            val temperature: TextView = view.findViewById(R.id.temperature)
            val temperature2: TextView = view.findViewById(R.id.temperature2)
            val temperature3: TextView = view.findViewById(R.id.temperature3)

            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
            data.text = LocalDate.now().format(dateFormatter)
            data2.text =  LocalDate.now().plusDays(1).format(dateFormatter)
            data3.text =  LocalDate.now().plusDays(2).format(dateFormatter)

            location.text =
                "${jsonObject.getJSONObject("city").getString("name")}, " +
                        "${jsonObject.getJSONObject("city").getString("country")}"

            weatherImage.setImageResource(WeatherIconsHelper().getImageByIconID(
                jsonObject.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon")))
            weatherImage2.setImageResource(WeatherIconsHelper().getImageByIconID(
                jsonObject.getJSONArray("list").getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("icon")))
            weatherImage3.setImageResource(WeatherIconsHelper().getImageByIconID(
                jsonObject.getJSONArray("list").getJSONObject(2).getJSONArray("weather").getJSONObject(0).getString("icon")))

            temperature.text =
                "${jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("temp_min")}ºC/" +
                        "${jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("temp_max")}ºC"
            temperature2.text =
                "${jsonObject.getJSONArray("list").getJSONObject(1).getJSONObject("main").getInt("temp_min")}ºC/" +
                        "${jsonObject.getJSONArray("list").getJSONObject(1).getJSONObject("main").getInt("temp_max")}ºC"
            temperature3.text =
                "${jsonObject.getJSONArray("list").getJSONObject(2).getJSONObject("main").getInt("temp_min")}ºC/" +
                        "${jsonObject.getJSONArray("list").getJSONObject(2).getJSONObject("main").getInt("temp_max")}ºC"

            ViewsHelper(context).showView(view)
        }

    }

}
