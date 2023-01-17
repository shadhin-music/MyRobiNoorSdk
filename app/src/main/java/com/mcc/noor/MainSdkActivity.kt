package com.mcc.noor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.util.handleClickEvent
import com.mcc.noor.databinding.ActivityMainSdkBinding

class MainSdkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var binding = ActivityMainSdkBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnHome.handleClickEvent {
            Noor.openNoor(this,"019838838")
        }

    }
}