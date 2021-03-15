package com.internshala.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.internshala.foodrunner.R

class PlaceOrderActivity : AppCompatActivity() {

    lateinit var btnOk: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        btnOk=findViewById(R.id.btnOk)

        btnOk.setOnClickListener{
            val intent= Intent(this@PlaceOrderActivity,NewActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onBackPressed() {

    }
}

