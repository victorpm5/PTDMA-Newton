package com.victor.newton.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.victor.newton.R
import java.util.*

class HomeFragment : Fragment(),TextToSpeech.OnInitListener {

    private lateinit var homeViewModel: HomeViewModel
    private var SPEECH_REQUEST_CODE: Int = 14
    private var resultat: TextView? = null
    private var tts: TextToSpeech? = null
    private var buttonReprodueix: Button? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        //Declarem coses de la UI
        val buttonGraba: FloatingActionButton = root.findViewById(R.id.button_graba)
        buttonReprodueix = root.findViewById(R.id.button_reprodueix)
        resultat = root.findViewById(R.id.speech)

        buttonReprodueix!!.isEnabled = false

        //Declarem TTS
        tts = TextToSpeech(root.context, this)

        //Associem accions
        buttonGraba.setOnClickListener {
            graba()
        }

        buttonReprodueix!!.setOnClickListener {
            reprodueixSo()
        }


        return root
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                buttonReprodueix!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

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
            resultat?.text = spokenText
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun reprodueixSo(){
        val text = resultat!!.text.toString()
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }



}
