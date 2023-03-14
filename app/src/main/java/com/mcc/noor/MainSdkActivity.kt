package com.mcc.noor

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.NoorAuthCallBack
import com.gakk.noorlibrary.util.handleClickEvent
import com.noor.R
import com.noor.databinding.ActivityMainSdkBinding


class MainSdkActivity : AppCompatActivity(), NoorAuthCallBack {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainSdkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login.handleClickEvent {

            val number = findViewById<EditText>(R.id.phone_number).text.trim().toString()
            if(number.isNotEmpty()){
                Noor.authNoor(this,number, this@MainSdkActivity)
            }else{
                Toast.makeText(this,"Enter number",Toast.LENGTH_SHORT).show()
            }

        }

        binding.homeBtn.handleClickEvent {

            Noor.openHome(this)
        }

        binding.quranBtn.handleClickEvent {

                Noor.openQuran(this)
        }

        binding.ramadanBtn.handleClickEvent {

            Noor.openRamadan(this)
        }

        binding.podcastBtn.handleClickEvent {

            Noor.openPodcast(this)
        }

        binding.hajjPreRegBtn.handleClickEvent {

            Noor.openHajjPreRegistration(this)
        }

    }

    override fun onDestroy() {
        Noor.destroySDK()
        super.onDestroy()
    }

    override fun onAuthSuccess() {
        Toast.makeText(this, "Auth Success Callback", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthFailed() {
        Toast.makeText(this, "Auth Failed Callback", Toast.LENGTH_SHORT).show()
    }
}