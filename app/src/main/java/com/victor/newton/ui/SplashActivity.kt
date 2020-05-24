package com.victor.newton.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.victor.newton.R
import com.victor.newton.helpers.LocationHelper
import com.victor.newton.services.PreferencesService


class SplashActivity : AppCompatActivity() {

    //Declarem Array de permisos
    private var permisos = arrayOf(Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.INTERNET)
    //Location
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Iniciem localització
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Comprovem permisos. Si no en té els demanem.
        if(comprovaPermisos(this)){
                goToMainActivity()
        }else {
            ActivityCompat.requestPermissions(this, permisos, 1)
        }
    }

    private fun comprovaPermisos(context: Context): Boolean {
        for(permis in permisos){
            if(ActivityCompat.checkSelfPermission(context, permis) == PackageManager.PERMISSION_DENIED) return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (comprovaGrantResults(grantResults)) {
                goToMainActivity()
        } else {

            //Avisem que s'han d'acceptar els permisos
            Toast.makeText(this, "You must accept the permissions to continue", Toast.LENGTH_SHORT).show()

            //Esperem 3 segons i els tornem a demanar
            val handler = Handler()
            handler.postDelayed({
                ActivityCompat.requestPermissions(this, permissions, 1)
            }, 3000)
        }
    }

    private fun comprovaGrantResults(grantResults: IntArray): Boolean {
        for(result in grantResults){
            if (result == PackageManager.PERMISSION_DENIED) return false
        }
        return true
    }

    private fun goToMainActivity(){

        inicialitza()

        val handler = Handler()
        //Esperem uns segons abans d'anar a la main activity
        handler.postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }, 3000)
    }


    //--------------------------------------Localització i inicialització-------------------------------------------

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun inicialitza() {

        if (isLocationEnabled()) {

            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    iniciaCamps( location.latitude, location.longitude)
                }
            }
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun getContext(): Context {
        return this
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            iniciaCamps(mLastLocation.latitude,mLastLocation.longitude)
        }
    }

    private fun iniciaCamps(latitude: Double, longitude: Double){

        val localitzacio = LocationHelper(getContext()).getCityByLatLong(latitude,longitude)
        val city = PreferencesService(this).getPreference("city")
        val unitats = PreferencesService(this).getPreference("unitats")

        //guardem la localitzacióActual
        PreferencesService(getContext()).savePreference("localitzacio",localitzacio.ciutat)

        //Si no hi ha cap defaultLocation posem la localització actual per defecte
        if(city == null){
            PreferencesService(this).savePreference("city", localitzacio.ciutat)
        }

        //Si no hi ha cap defaultLocation posem unitats mètriques per defecte
        if(unitats == null){
            PreferencesService(this).savePreference("unitats", "metric")
        }
    }


}
