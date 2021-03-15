package com.internshala.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var txtEnter: TextView
    private lateinit var etOTP: EditText
    private lateinit var etPassword3: EditText
    private lateinit var etConfirmPassword3: EditText
    private lateinit var btnSubmit: Button
    private lateinit var rlContent3: RelativeLayout
    private lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        txtEnter = findViewById(R.id.txtEnter)
        etOTP = findViewById(R.id.etOTP)
        etPassword3 = findViewById(R.id.etPassword3)
        etConfirmPassword3 = findViewById(R.id.etConfirmPassword3)
        btnSubmit = findViewById(R.id.btnSubmit)
        rlContent3 = findViewById(R.id.rlContent3)

        rlContent3.visibility = View.VISIBLE
        if (intent != null) {
            mobileNumber = intent.getStringExtra("user_mobile") as String
        }
        btnSubmit.setOnClickListener {
            rlContent3.visibility = View.GONE
            if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {
                if (etOTP.text.length == 4) {
                    if (etPassword3.length()>=4) {
                        if (etPassword3.text.toString()==etConfirmPassword3.text.toString())
                        {
                            resetPassword(
                                mobileNumber,
                                etOTP.text.toString(),
                                etPassword3.text.toString()
                            )
                        } else {
                            rlContent3.visibility = View.VISIBLE
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                "Passwords do not match",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        rlContent3.visibility = View.VISIBLE
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Invalid Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    rlContent3.visibility = View.VISIBLE
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Incorrect OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }
                dialog.setNegativeButton("Exit") { text, listener ->

                    ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    private fun resetPassword(mobileNumber: String, otp: String, password: String) {
        val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", password)
        jsonParams.put("otp", otp)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                try {
                    val obj = it.getJSONObject("data")
                    val success = obj.getBoolean("success")
                    if (success) {
                        val builder = AlertDialog.Builder(this@ResetPasswordActivity)
                        builder.setTitle("Confirmation")
                        builder.setMessage("Your Password has been successfully changed")
                        builder.setCancelable(false)
                        builder.setPositiveButton("Ok") { _, _ ->
                            startActivity(
                                Intent(
                                    this@ResetPasswordActivity,
                                    MainActivity::class.java
                                )
                            )
                            ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                        }
                        builder.create().show()
                    } else {
                        rlContent3.visibility = View.VISIBLE
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Some error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    rlContent3.visibility = View.VISIBLE
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Some exception occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                rlContent3.visibility = View.VISIBLE
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Volley error occurred!",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9326ad19a377d6"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }

}
