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


class SplashActivity : AppCompatActivity() {

    //Declarem Array de permisos
    private var permisos = arrayOf(Manifest.permission.RECORD_AUDIO)

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

        val handler = Handler()

        //Esperem uns segons abans d'anar a la main activity
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }, 3000)
    }

}
