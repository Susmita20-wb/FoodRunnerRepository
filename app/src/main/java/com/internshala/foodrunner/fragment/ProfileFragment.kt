package com.internshala.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.SessionManager

class ProfileFragment :Fragment(){

    lateinit var txtUserName:TextView
    lateinit var txtPhone:TextView
    lateinit var txtEmail:TextView
    lateinit var txtAddress:TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_profile,container,false)

        sessionManager= SessionManager(activity as Context)
        sharedPreferences=(activity as FragmentActivity).getSharedPreferences(sessionManager.PREF_NAME,Context.MODE_PRIVATE)
        txtUserName=view.findViewById(R.id.txtUserName)
        txtPhone=view.findViewById(R.id.txtPhone)
        txtEmail=view.findViewById(R.id.txtEmail)
        txtAddress=view.findViewById(R.id.txtAddress)

        txtUserName.text=sharedPreferences.getString("user_name",null)
        val phoneText="+91-${sharedPreferences.getString("user_mobile_number",null)}"
        txtPhone.text=phoneText
        txtEmail.text=sharedPreferences.getString("user_email",null)
        txtAddress.text=sharedPreferences.getString("user_address",null)

        return view
    }
}
