package com.victor.newton.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.victor.newton.BuildConfig
import com.victor.newton.R
import com.victor.newton.helpers.LocationHelper
import com.victor.newton.helpers.ViewsHelper
import com.victor.newton.helpers.WeatherIconsHelper
import com.victor.newton.services.PreferencesService
import com.victor.newton.services.WeatherService
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class HomeActivity : AppCompatActivity(),TextToSpeech.OnInitListener {

    private var SPEECH_REQUEST_CODE: Int = 14
    private var tts: TextToSpeech? = null
    //Location
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Declarem barra de navegació
        initNavigationBar()

        //Iniciem botó grabació
        val buttonGraba: FloatingActionButton = findViewById(R.id.button_graba)
        tts = TextToSpeech(this, this)

        buttonGraba.setOnClickListener {
            graba()
        }

        //Iniciem localització
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Visualitzacio Inicial
        amagaVistes()
        mostraInfoInicial()
    }

    private fun initNavigationBar() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnNavigationItemSelectedListener {

            if (it.itemId == R.id.navigation_settings) {
                val intent = Intent(this, SettingsActivity::class.java)
                this.startActivity(intent)
                this.finish()
                overridePendingTransition(0, 0)
            }

            true
        }
    }

    //------------------------------------------- Voice Recognition methods --------------------------------------------
    private fun graba(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en")
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val results: List<String> = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results[0]

            if(spokenText.trim() != ""){
                amagaVistes()
                procesa(spokenText)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //------------------------------------------------- TTS -------------------------------------------------------------

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            //Indiquem English,US com a llengua
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    private fun reprodueixSo(missatge: String){
        tts!!.speak(missatge, TextToSpeech.QUEUE_FLUSH, null,"")
    }


    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    //------------------------------------------------- Location -------------------------------------------------------------

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
//        if (checkPermissions()) {
        if (isLocationEnabled()) {

            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    actualitzaVistaLocation( location.latitude, location.longitude)
                }
            }
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
//        } else {
//            requestPermissions()
//        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        //Check new location every 5-10 seconds
//        mLocationRequest.interval = 10000
//        mLocationRequest.fastestInterval = 5000

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            actualitzaVistaLocation(mLastLocation.latitude,mLastLocation.longitude)
        }
    }


    private fun actualitzaVistaLocation(latitude: Double, longitude: Double){
        val locationView: View = findViewById(R.id.currentLocation)
        val text: TextView = locationView.findViewById(R.id.textMissatge)
        val imatgeLocation: ImageView = locationView.findViewById(R.id.icon)

        val city = LocationHelper(this).getCityByLatLong(latitude, longitude)

        val location = "$city${System.getProperty ("line.separator")}($latitude,$longitude)"
        text.text = location
        imatgeLocation.setImageResource(R.drawable.my_location)

        reprodueixSo("You are currently in $city")
        ViewsHelper(this).showView(locationView)
    }


    //--------------------------------------------------------- PROCESSA -----------------------------------------------------------------

    fun procesa (text: String){

        val lowerText = text.toLowerCase(Locale.ROOT)

        //What is my location? Where am I?
        if((lowerText.startsWith("what") && lowerText.contains("my location"))
            || lowerText.startsWith("where am i")){
            getLastLocation()
            viewMessage("Query successful: current location",true)
        }
        //What is my default location? What is the default location?
        else if((lowerText.startsWith("what") && lowerText.contains("default location"))){

            val city = PreferencesService(this).getPreference("city")
            city?.let { viewDefaultLocation(it) }

            reprodueixSo("Your default location is $city")
            viewMessage("Query successful: default location",true)
        }
        //Set default location to...
        else if(lowerText.startsWith("set default location to")){

            val ciutat = lowerText.split("location to")[1].trim()

            //TODO workflow si valor ciutat = "my location", "my current location"...
            PreferencesService(this).savePreference("city", ciutat)

            val city = PreferencesService(this).getPreference("city")
            city?.let { viewDefaultLocation(it) }

            viewMessage("Change completed successfully",true)
            reprodueixSo("Change completed successfully, Your default location now is $ciutat")

        }
        //What are my events? What are my events for today? what events do I have? What are my events for tomorrow?
        else if((lowerText.startsWith("what") && lowerText.contains("event"))){

            if(lowerText.contains("events for")){
                val day = lowerText.split("events for")[1].trim()

                //TODO get events

                reprodueixSo("Your events for $day are: meeting at 5, meeting at 6 and meeting at 7")
                viewMessage("Events for $day obtained successfully",true)
            }else{
                reprodueixSo("Your events for today are: meeting at 5, meeting at 6 and meeting at 7")
                viewMessage("Events for today obtained successfully",true)
            }

            val calendar: View = findViewById(R.id.events)
            ViewsHelper(this).showView(calendar)

        }
        //Create new event, create event, ...
        else if((lowerText.startsWith("create") && lowerText.contains("event"))) {
            viewMessage("Event created successfully",true)
            reprodueixSo("Event created successfully")

            val calendar: View = findViewById(R.id.events)
            ViewsHelper(this).showView(calendar)
        }
        //what is the weather? what's the weather?
        else if((lowerText == "what is the weather") || lowerText == "what's the weather") {

            PreferencesService(this).getPreference("city")?.let { WeatherService(this).getCurrentWeatherByLocation(null, null,it,findViewById(R.id.weather)) }

            viewMessage("Query successful: weather",true)
            //TODO: Canviar TTS i dir el temps en la localització
            reprodueixSo("Showing you the weather for your default location")
        }
        //What is the weather in...?
        else if((lowerText.startsWith("what is the weather in") || lowerText.startsWith("what's the weather in"))) {

            val city = lowerText.split("weather in")[1].trim()

            //TODO workflow si valor ciutat = "my location", "my current location"...
            WeatherService(this).getCurrentWeatherByLocation(null, null,city,findViewById(R.id.weather))

            viewMessage("Query successful: weather in $city",true)
            //TODO: Canviar TTS i dir el temps en la localització
            reprodueixSo("Showing you the weather in $city")
        }
        //what will be the weather tomorrow?what is the weather for tomorrow?
        else if(lowerText == "what will be the weather tomorrow"
            || lowerText == "what is the weather for tomorrow" || lowerText == "what's the weather for tomorrow") {

            //TODO check weather for tomorrow
            PreferencesService(this).getPreference("city")?.let { WeatherService(this).getCurrentWeatherByLocation(null, null,it,findViewById(R.id.weather)) }

            viewMessage("Query successful: weather for tomorrow",true)
            //TODO: Canviar TTS i dir el temps en la localització
            reprodueixSo("Showing you the weather for tomorrow in your default location")
        }
        //What is the weather forecast?
        else if(lowerText == "what is the weather forecast" || lowerText == "what's the weather forecast") {

            PreferencesService(this).getPreference("city")?.let { WeatherService(this).getForecastWeatherByLocation(null,null, it,findViewById(R.id.weatherForecast)) }

            viewMessage("Query successful: weather forecast",true)
            //TODO: Canviar TTS i dir el temps en la localització
            reprodueixSo("Showing you the weather forecast for your default location")

        }
        //What is the weather forecast in...?
        else if((lowerText.startsWith("what is the weather forecast in") || lowerText.startsWith("what's the weather forecast in"))) {

            val city = lowerText.split("forecast in")[1].trim()
            WeatherService(this).getForecastWeatherByLocation(null,null, city,findViewById(R.id.weatherForecast))

            viewMessage("Query successful: weather forecast in $city",true)
            //TODO: Canviar TTS i dir el temps en la localització
            reprodueixSo("Showing you the weather forecast in $city")

        }
        //Show My information
        else if(lowerText == "show my information"){

            mostraInfoInicial()

            viewMessage("Query successful: show my information",true)
            reprodueixSo("Showing your basic information")
        }
        //Other type of message...
        else{
            viewMessage("Sorry, I didn't understand what you said ", false)
            reprodueixSo("Sorry, I didn't understand what you said")
        }

    }


    //---------------------------------------VIEWS --------------------------------------------------------------

    fun amagaVistes(){
        val messageTest: View = findViewById(R.id.message2)
        val defaultLocationView: View = findViewById(R.id.defaultLocation)
        val locationView: View = findViewById(R.id.currentLocation)
        val weatherView: View = findViewById(R.id.weather)
        val weatherForecastView: View = findViewById(R.id.weatherForecast)
        val eventsView: View = findViewById(R.id.events)

        messageTest.visibility = View.GONE
        defaultLocationView.visibility = View.GONE
        locationView.visibility = View.GONE
        weatherView.visibility = View.GONE
        weatherForecastView.visibility = View.GONE
        eventsView.visibility = View.GONE
    }

    fun mostraInfoInicial(){
        val city = PreferencesService(this).getPreference("city")
        city?.let { viewDefaultLocation(it) }
        city?.let { WeatherService(this).getCurrentWeatherByLocation(null, null,it,findViewById(R.id.weather))}

        val calendar: CalendarView = findViewById(R.id.calendar)
        calendar.maxDate = calendar.date
        calendar.minDate = calendar.date

        val events: View = findViewById(R.id.events)
        ViewsHelper(this).showView(events)
    }

    fun viewMessage(textOk :String, ok: Boolean){
        val messageTest: View = findViewById(R.id.message2)
        val imatge: ImageView = messageTest.findViewById(R.id.icon)
        val text: TextView = messageTest.findViewById(R.id.textMissatge)

        text.text = textOk

        if(ok) imatge.setImageResource(R.drawable.ok)
        else imatge.setImageResource(R.drawable.error2)

        ViewsHelper(this).showView(messageTest)
        ViewsHelper(this).hideView(messageTest)
    }

    fun viewDefaultLocation(city: String){
        val defaultLocationView: View = findViewById(R.id.defaultLocation)
        val locationText: TextView = defaultLocationView.findViewById(R.id.textMissatge)
        val imatge: ImageView = defaultLocationView.findViewById(R.id.icon)

        imatge.setImageResource(R.drawable.location)
        locationText.text = "${city.capitalize()}${System.getProperty ("line.separator")}(Default location)"

        ViewsHelper(this).showView(defaultLocationView)
    }

}
