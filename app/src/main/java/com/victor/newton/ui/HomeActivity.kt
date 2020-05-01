package com.victor.newton.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.isPresent
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.victor.newton.R
import java.util.*
import com.google.android.gms.location.*


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

        val buttonGraba: FloatingActionButton = findViewById(R.id.button_graba)
        tts = TextToSpeech(this, this)

        buttonGraba.setOnClickListener {
            graba()
        }

        //TEST Declarem múltiples missatges error

        val messageTest: View = findViewById(R.id.message2)
        val imatge: ImageView = messageTest.findViewById<ImageView>(R.id.icon)
        val text: TextView = messageTest.findViewById<TextView>(R.id.textMissatge)
        imatge.setImageResource(R.drawable.ok)
        text.setText("Change completed successfully")
        text.textSize

        val messageTest2: View = findViewById(R.id.message3)
        val imatge2: ImageView = messageTest2.findViewById<ImageView>(R.id.icon)
        val text2: TextView = messageTest2.findViewById<TextView>(R.id.textMissatge)
        imatge2.setImageResource(R.drawable.error2)
        text2.setText("Ups, there has been an error updating the field")

        //TEST with calendar
        val calendar: CalendarView = findViewById(R.id.calendar)
        calendar.maxDate = calendar.date
        calendar.minDate = calendar.date


        //TEST LOCATION
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

    }

    private fun initNavigationBar() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId =
            R.id.navigation_home

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
            reprodueixSo("You've said: " + spokenText)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //------------------------------------------------- TTS -------------------------------------------------------------

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
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

    private fun getCityByLatLong(latitude: Double, Longitude: Double): String{
        val geocoder = Geocoder(this, Locale.getDefault())

        var cityName = ""
        var countryCode = ""

        if(isPresent()) {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, Longitude, 1)
            cityName = addresses[0].locality
            countryCode = addresses[0].countryCode
        }

        return "$cityName, $countryCode"
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
//        if (checkPermissions()) {
        if (isLocationEnabled()) {

            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                var location: Location? = task.result
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
        val messageTest: View = findViewById(R.id.message1)
        val text: TextView = messageTest.findViewById<TextView>(R.id.textMissatge)

        val location = "${getCityByLatLong(latitude, longitude)}${System.getProperty ("line.separator")}($latitude,$longitude)"
        text.text = location

    }


}
