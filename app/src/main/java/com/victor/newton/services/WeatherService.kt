package com.victor.newton.services


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.victor.newton.BuildConfig
import com.victor.newton.R
import com.victor.newton.domain.ForecastWeather
import com.victor.newton.domain.OneDayWeather
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


    fun getCurrentWeatherByLocation(latitude: Double?, longitude: Double?, city: String, view: View, isVistaGlobal: Boolean) {

        val url = generaUrl(weatherURL,latitude,longitude,city)

        val request = Request.Builder().url(url).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                TextToSpeechService(context, "Sorry, it has not been possible to obtain the weather")
            }

            override fun onResponse(call: Call, response: Response) {

                if(response.isSuccessful) {

                    val jsonData = response.body()?.string()
                    val jsonObject = JSONObject(jsonData)

                    actualitzaVistaWeather(view, mapejaOneDayWeather(jsonObject, false), isVistaGlobal)
                }else {
                    TextToSpeechService(context, "Sorry, it has not been possible to obtain the weather")
                }
            }
        })
    }

    fun getForecastWeatherByLocation(latitude: Double?, longitude: Double?, city:String, view: View, tomorrow: Boolean) {

        val url = generaUrl(forecastURL,latitude,longitude,city)

        val request = Request.Builder().url(url).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                TextToSpeechService(context, "Sorry, it has not been possible to obtain the weather")
            }

            override fun onResponse(call: Call, response: Response) {

                if(response.isSuccessful) {

                    val jsonData = response.body()?.string()
                    val jsonObject = JSONObject(jsonData)

                    if (tomorrow) {
                        actualitzaVistaWeather(view, mapejaOneDayWeather(jsonObject, true), false)
                    } else {
                        actualitzaVistaForecast(view, mapejaForecastWeather(jsonObject))
                    }
                } else {
                    TextToSpeechService(context, "Sorry, it has not been possible to obtain the weather")
                }
            }
        })
    }

    private fun generaUrl(base: String,latitude: Double?, longitude: Double?, city:String) :HttpUrl{

        val unitats = PreferencesService(context).getPreference("unitats")

        val url = HttpUrl.parse(base)!!.newBuilder()
            .addQueryParameter("APPID", apiKey)
            .addQueryParameter("units", unitats)

        if(city.trim() != ""){
            url.addQueryParameter("q", city)
        }else {
            url.addQueryParameter("lat", latitude.toString())
                .addQueryParameter("lon", longitude.toString())
        }

        return url.build()
    }

    private fun mapejaOneDayWeather(jsonObject: JSONObject, isObjectForecast: Boolean):OneDayWeather{

        val oneDayWeather = OneDayWeather()
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")

        if(isObjectForecast){
            oneDayWeather.data = LocalDate.now().plusDays(1).format(dateFormatter)
            oneDayWeather.localitzacio = jsonObject.getJSONObject("city").getString("name")
            oneDayWeather.temperatura  = "${jsonObject.getJSONArray("list").getJSONObject(8).getJSONObject("main").getInt("temp")}ºC"
            oneDayWeather.minTemp = "${jsonObject.getJSONArray("list").getJSONObject(8).getJSONObject("main").getInt("temp_min")}ºC"
            oneDayWeather.maxTemp = "${jsonObject.getJSONArray("list").getJSONObject(8).getJSONObject("main").getInt("temp_max")}ºC"
            oneDayWeather.descripcio = jsonObject.getJSONArray("list").getJSONObject(8).getJSONArray("weather").getJSONObject(0).getString("description")
            oneDayWeather.humitat = "${jsonObject.getJSONArray("list").getJSONObject(8).getJSONObject("main").getInt("humidity")}% Hum."
            oneDayWeather.weatherImage =  WeatherIconsHelper().getImageByIconID(
                jsonObject.getJSONArray("list").getJSONObject(8).getJSONArray("weather").getJSONObject(0).getString("icon"))
            oneDayWeather.isTomorrow = true
        } else {
            oneDayWeather.data = LocalDate.now().format(dateFormatter)
            oneDayWeather.localitzacio = jsonObject.getString("name")
            oneDayWeather.temperatura  = "${jsonObject.getJSONObject("main").getInt("temp")}ºC"
            oneDayWeather.minTemp = "${jsonObject.getJSONObject("main").getInt("temp_min")}ºC"
            oneDayWeather.maxTemp = "${jsonObject.getJSONObject("main").getInt("temp_max")}ºC"
            oneDayWeather.descripcio = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
            oneDayWeather.humitat = "${jsonObject.getJSONObject("main").getInt("humidity")}% Hum."
            oneDayWeather.weatherImage =  WeatherIconsHelper().getImageByIconID(jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon"))
        }
        return oneDayWeather
    }

    private fun mapejaForecastWeather(jsonObject: JSONObject):ForecastWeather {

        val forecastWeather = ForecastWeather()
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")

        forecastWeather.localitzacio = jsonObject.getJSONObject("city").getString("name")

        forecastWeather.data1 = LocalDate.now().format(dateFormatter)
        forecastWeather.data2 =  LocalDate.now().plusDays(1).format(dateFormatter)
        forecastWeather.data3 =  LocalDate.now().plusDays(2).format(dateFormatter)

        forecastWeather.temperatura1 =
            "${jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("temp_min")}ºC/" +
                    "${jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("temp_max")}ºC"
        forecastWeather.temperatur2 =
            "${jsonObject.getJSONArray("list").getJSONObject(8).getJSONObject("main").getInt("temp_min")}ºC/" +
                    "${jsonObject.getJSONArray("list").getJSONObject(8).getJSONObject("main").getInt("temp_max")}ºC"
        forecastWeather.temperatura3 =
            "${jsonObject.getJSONArray("list").getJSONObject(16).getJSONObject("main").getInt("temp_min")}ºC/" +
                    "${jsonObject.getJSONArray("list").getJSONObject(16).getJSONObject("main").getInt("temp_max")}ºC"

        forecastWeather.weatherImage1 = WeatherIconsHelper().getImageByIconID(
            jsonObject.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon"))
        forecastWeather.weatherImage2 = WeatherIconsHelper().getImageByIconID(
            jsonObject.getJSONArray("list").getJSONObject(8).getJSONArray("weather").getJSONObject(0).getString("icon"))
        forecastWeather.weatherImage3 = WeatherIconsHelper().getImageByIconID(
            jsonObject.getJSONArray("list").getJSONObject(16).getJSONArray("weather").getJSONObject(0).getString("icon"))

        forecastWeather.descripcio1 = jsonObject.getJSONArray("list").getJSONObject(0)
            .getJSONArray("weather").getJSONObject(0).getString("description")
        forecastWeather.descripcio2 = jsonObject.getJSONArray("list").getJSONObject(8)
            .getJSONArray("weather").getJSONObject(0).getString("description")
        forecastWeather.descripcio3 = jsonObject.getJSONArray("list").getJSONObject(16)
            .getJSONArray("weather").getJSONObject(0).getString("description")


        return  forecastWeather
    }



    private fun actualitzaVistaWeather(view: View, oneDayWeather: OneDayWeather, isVistaGlobal: Boolean){

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

            data.text = oneDayWeather.data
            location.text = oneDayWeather.localitzacio
            temperatura.text = oneDayWeather.temperatura
            minTemp.text = oneDayWeather.minTemp
            maxTemp.text = oneDayWeather.maxTemp
            description.text = oneDayWeather.descripcio
            humidity.text = oneDayWeather.humitat
            weatherImage.setImageResource(oneDayWeather.weatherImage)

            ViewsHelper(context).showView(view)
            if(!isVistaGlobal) TextToSpeechService(context, missatgeWeather(oneDayWeather))
        }

    }

    private fun missatgeWeather(oneDayWeather: OneDayWeather) :String{

        var missatge = "The weather for"

        //Data
        if(oneDayWeather.isTomorrow) missatge += " tomorrow"
        else missatge += " today"

        missatge += " in ${oneDayWeather.localitzacio} is ${oneDayWeather.descripcio} with a temperature of ${oneDayWeather.temperatura} degrees"

        return missatge
    }

    private fun actualitzaVistaForecast(view: View, forecastWeather: ForecastWeather) {

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

            location.text = forecastWeather.localitzacio
            data.text = forecastWeather.data1
            data2.text =  forecastWeather.data2
            data3.text =  forecastWeather.data3
            weatherImage.setImageResource(forecastWeather.weatherImage1)
            weatherImage2.setImageResource(forecastWeather.weatherImage2)
            weatherImage3.setImageResource(forecastWeather.weatherImage3)
            temperature.text = forecastWeather.temperatura1
            temperature2.text = forecastWeather.temperatur2
            temperature3.text = forecastWeather.temperatura3

            ViewsHelper(context).showView(view)
            TextToSpeechService(context, missatgeForecast(forecastWeather))
        }

    }

    private fun missatgeForecast(forecastWeather: ForecastWeather) :String{

        val missatge = "The weather forecast in ${forecastWeather.localitzacio} is ${forecastWeather.descripcio1} for today, " +
                "${forecastWeather.descripcio2} for tomorrow and ${forecastWeather.descripcio3} for the day after tomorrow"

        return missatge
    }

}
