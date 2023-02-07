package com.mcc.noor

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.util.handleClickEvent
import com.noor.R
import com.noor.databinding.ActivityMainSdkBinding

class MainSdkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var binding = ActivityMainSdkBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnHome.handleClickEvent {

            val number = findViewById<EditText>(R.id.phone_number).text.trim().toString()
            if(number.isNotEmpty()){
                Noor.openNoor(this,number)
            }else{
                Toast.makeText(this,"Enter number",Toast.LENGTH_SHORT).show()
            }


        }

    }

    override fun onDestroy() {
        Noor.destroySDK()
        super.onDestroy()
    }
}