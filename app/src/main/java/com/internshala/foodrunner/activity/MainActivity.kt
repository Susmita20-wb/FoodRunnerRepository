package com.internshala.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.ConnectionManager
import com.internshala.foodrunner.util.SessionManager
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogIn: Button
    lateinit var txtForgot: TextView
    lateinit var txtRegister: TextView
    var mobilePattern="[7-9][0-9]{9}"
    lateinit var sessionManager: SessionManager
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sessionManager= SessionManager(this@MainActivity)
        sharedPreferences=this.getSharedPreferences(sessionManager.PREF_NAME,Context.MODE_PRIVATE)

        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_main)
         if (isLoggedIn){
             val intent=Intent(this@MainActivity,NewActivity::class.java)
             startActivity(intent)
             finish()
         }

        etMobileNumber=findViewById(R.id.etMobileNumber)
        etPassword=findViewById(R.id.etPassword)
        btnLogIn=findViewById(R.id.btnLogIn)
        txtForgot=findViewById(R.id.txtForgot)
        txtRegister=findViewById(R.id.txtRegister)

        txtRegister.setOnClickListener{
            val intent=Intent(this@MainActivity,RegisterActivity::class.java)
            startActivity(intent)
        }
        txtForgot.setOnClickListener{
            val intent= Intent(this@MainActivity,ForgotActivity::class.java)
            startActivity(intent)
        }

        btnLogIn.setOnClickListener{
            if (validations(etMobileNumber.text.toString(),etPassword.text.toString())){
                if(ConnectionManager().checkConnectivity(this@MainActivity)){
                    val queue= Volley.newRequestQueue(this@MainActivity)
                    val url="http://13.235.250.119/v2/login/fetch_result/"
                    val jsonParams=JSONObject()
                    jsonParams.put("mobile_number",etMobileNumber.text.toString())
                    jsonParams.put("password",etPassword.text.toString())

                    val jsonObjectRequest=
                        object : JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {
                            try{
                                val data=it.getJSONObject("data")
                                val success=data.getBoolean("success")
                                if(success){
                                    btnLogIn.isEnabled=false
                                    btnLogIn.isClickable=false

                                    val response=data.getJSONObject("data")
                                    sharedPreferences.edit().putString("user_id",response.getString("user_id")).apply()
                                    sharedPreferences.edit().putString("user_name",response.getString("name")).apply()
                                    sharedPreferences.edit().putString("user_mobile_number",response.getString("mobile_number")).apply()
                                    sharedPreferences.edit().putString("user_address",response.getString("address")).apply()
                                    sharedPreferences.edit().putString("user_email",response.getString("email")).apply()

                                    sessionManager.setLogin(true)
                                    startActivity(Intent(this@MainActivity,NewActivity::class.java))
                                    finish()
                                }
                                else{
                                    Toast.makeText(this@MainActivity,"Invalid Credentials",Toast.LENGTH_SHORT).show()
                                }
                            }
                            catch (e:JSONException){
                                e.printStackTrace()
                            }
                    },Response.ErrorListener {
                            Toast.makeText(this@MainActivity,"Volley Error Occured",Toast.LENGTH_SHORT).show()
                        }){
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers=HashMap<String,String>()
                                headers["Content-type"]="application/json"
                                headers["token"]="9326ad19a377d6"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                }
                else{
                    val dialog= AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection not Found")
                    dialog.setPositiveButton("Open Settings"){text, listener->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this?.finish()
                    }
                    dialog.setNegativeButton("Exit"){text, listener->
                        ActivityCompat.finishAffinity(this@MainActivity)
                    }
                    dialog.create()
                    dialog.show()
                }

            }
        }

    }

    override fun onPause() {
        super.onPause()

        var login=sharedPreferences.getBoolean("isLoggedIn",true)
        if(login){
            finish()
        }
    }

    fun validations(phone:String,password:String):Boolean {
        if (phone.isEmpty()) {
            Toast.makeText(this@MainActivity, "Enter Mobile Number", Toast.LENGTH_LONG).show()
            return false
        } else {
            if (password.isEmpty()) {
                Toast.makeText(this@MainActivity, "Enter Password", Toast.LENGTH_LONG).show()
                return false
            } else {
                if (!phone.trim().matches(mobilePattern.toRegex())) {
                    Toast.makeText(
                        this@MainActivity,
                        "Enter a valid mobile number",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                } else {
                    return true
                }
            }
        }
    }

}