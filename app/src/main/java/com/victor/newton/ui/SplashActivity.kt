package com.victor.newton.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.victor.newton.R
import com.victor.newton.services.PreferencesService


class SplashActivity : AppCompatActivity() {

    //Declarem Array de permisos
    private var permisos = arrayOf(Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.INTERNET)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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

        inicialitzaPreferencesSiEscau()

        val handler = Handler()
        //Esperem uns segons abans d'anar a la main activity
        handler.postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }, 3000)
    }

    private fun inicialitzaPreferencesSiEscau(){
        
        val city = PreferencesService(this).getPreference("city")
        val unitats = PreferencesService(this).getPreference("unitats")

        //TODO canviar i ficar localització actual del dispositiu
        //Si no hi ha cap defaultLocation posem Barcelona per defecte
        if(city == null){
            PreferencesService(this).savePreference("city", "Barcelona")
        }

        if(unitats == null){
            PreferencesService(this).savePreference("unitats", "metric")
        }

    }


}
