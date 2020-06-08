package com.victor.newton.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.victor.newton.R
import com.victor.newton.services.PreferencesService


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ompleDadesInicials()
        initNavigationBar()
    }

    private fun initNavigationBar() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId =
            R.id.navigation_settings

        bottomNavigationView.setOnNavigationItemSelectedListener {

            if (it.itemId == R.id.navigation_home) {
                this.finish()
                overridePendingTransition(0, 0)
            }

            true
        }
    }

    override fun onBackPressed() {
        this.finish()
        overridePendingTransition(0, 0)
    }

    private fun ompleDadesInicials(){

        var preferenceView: View = findViewById(R.id.DefaultCity1)
        var textTitol: TextView = preferenceView.findViewById(R.id.textTitol)
        var textContingut: TextView = preferenceView.findViewById(R.id.textValor)
        var imatge: ImageView = preferenceView.findViewById(R.id.icon)

        textTitol.text = "Primary default city"

        val city = PreferencesService(this).getPreference("city")
        city?.let { textContingut.text = it.capitalize() }

        imatge.setOnClickListener {
            actualitzaDefaultCity("city",city)
        }

        preferenceView  = findViewById(R.id.DefaultCity2)
        textTitol = preferenceView.findViewById(R.id.textTitol)
        textContingut = preferenceView.findViewById(R.id.textValor)
        imatge = preferenceView.findViewById(R.id.icon)

        textTitol.text = "Secondary default city"

        val city2 = PreferencesService(this).getPreference("city2")
        city2?.let { textContingut.text = it.capitalize() }

        imatge.setOnClickListener {
            actualitzaDefaultCity("city2",city2)
        }

        preferenceView  = findViewById(R.id.Units)
        textTitol = preferenceView.findViewById(R.id.textTitol)
        textContingut = preferenceView.findViewById(R.id.textValor)
        imatge = preferenceView.findViewById(R.id.icon)

        textTitol.text = "Units"

        val units = PreferencesService(this).getPreference("unitats")
        units?.let { textContingut.text = it.capitalize() }

        imatge.setOnClickListener {
            actualitzaUnitats(textContingut)
        }


    }

    private fun actualitzaDefaultCity(key: String, value: String?){

        val builder = AlertDialog.Builder(this)

        if(key.equals("city")) {
            builder.setTitle("Primary default city")
        }else {
            builder.setTitle("Secondary default city")
        }

        val constraintLayout = getEditTextLayout(this)
        builder.setView(constraintLayout)

        val textInputLayout = constraintLayout.
            findViewWithTag<TextInputLayout>("textInputLayoutTag")
        val textInputEditText = constraintLayout.
            findViewWithTag<TextInputEditText>("textInputEditTextTag")

        // alert dialog positive button
        builder.setPositiveButton("Submit"){dialog,which->
            val preferenceValue = textInputEditText.text
            PreferencesService(this).savePreference(key, preferenceValue.toString())
        }

        // alert dialog other buttons
        builder.setNegativeButton("Cancel",null)
//        builder.setNeutralButton("Cancel",null)

        val dialog = builder.create()

        dialog.show()


        if(key.equals("city")) {

            // initially disable the positive button
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

            // edit text text change listener
            textInputEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(
                    p0: CharSequence?, p1: Int,
                    p2: Int, p3: Int
                ) {
                }

                override fun onTextChanged(
                    p0: CharSequence?, p1: Int,
                    p2: Int, p3: Int
                ) {
                    if (p0.isNullOrBlank()) {
                        textInputLayout.error = "This field is required, you must provide a valid city name"
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .isEnabled = false
                    } else {
                        textInputLayout.error = ""
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .isEnabled = true
                    }
                }
            })
        }


    }

    fun getEditTextLayout(context: Context): ConstraintLayout {
        val constraintLayout = ConstraintLayout(context)
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.layoutParams = layoutParams
        constraintLayout.id = View.generateViewId()

        val textInputLayout = TextInputLayout(context)
        layoutParams.setMargins(
            32.toDp(context),
            8.toDp(context),
            32.toDp(context),
            8.toDp(context)
        )
        textInputLayout.layoutParams = layoutParams
        textInputLayout.hint = "Input city name"
        textInputLayout.id = View.generateViewId()
        textInputLayout.tag = "textInputLayoutTag"


        val textInputEditText = TextInputEditText(context)
        textInputEditText.id = View.generateViewId()
        textInputEditText.tag = "textInputEditTextTag"

        textInputLayout.addView(textInputEditText)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintLayout.addView(textInputLayout)
        return constraintLayout
    }


    // extension method to convert pixels to dp
    fun Int.toDp(context: Context):Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
    ).toInt()

    private fun actualitzaUnitats(contingut :TextView){
        val units = arrayOf<CharSequence>("Metric (Celsius)", "Imperial (Fahrenheit)", "Standard (Kelvin)")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Weather units")
            .setItems(units) { _, which ->
                when(which){
                    0 -> PreferencesService(this).savePreference("unitats", "metric")
                    1 -> PreferencesService(this).savePreference("unitats", "imperial")
                    2 -> PreferencesService(this).savePreference("unitats", "standard")
                }

                val units = PreferencesService(this).getPreference("unitats")
                units?.let { contingut.text = it.capitalize() }
            }

        val dialog = builder.create()
        dialog.show()
    }


}
