package com.victor.newton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnNavigationItemSelectedListener {

            if(it.itemId == R.id.navigation_settings){
                val intent = Intent(this, SettingsActivity::class.java)
                this.startActivity(intent)
                this.finish()
                overridePendingTransition(0,0)
            }

            true
        }
    }
}
