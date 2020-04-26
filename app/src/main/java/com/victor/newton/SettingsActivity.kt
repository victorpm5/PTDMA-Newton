package com.victor.newton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.navigation_settings

        bottomNavigationView.setOnNavigationItemSelectedListener {

            if(it.itemId == R.id.navigation_home){
                val intent = Intent(this, HomeActivity::class.java)
                this.startActivity(intent)
                this.finish()
                overridePendingTransition(0,0)
            }

            true
        }
    }
}
