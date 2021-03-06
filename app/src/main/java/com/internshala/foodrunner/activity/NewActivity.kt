package com.internshala.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.fragment.*
import com.internshala.foodrunner.util.SessionManager

class NewActivity : AppCompatActivity() {

    lateinit var drawerLayout:DrawerLayout
    lateinit var coordinateLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sessionManager: SessionManager
    var previousMenuItem:MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        title="All Restaurants"

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinateLayout=findViewById(R.id.coordinateLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)
        sessionManager= SessionManager(this@NewActivity)
        sharedPreferences=getSharedPreferences(sessionManager.PREF_NAME, Context.MODE_PRIVATE)

        setUpToolbar()
        openHome()

        val view= LayoutInflater.from(this@NewActivity).inflate(R.layout.drawer_header,null)
        val userName:TextView=view.findViewById(R.id.txtUserName)
        val userPhone:TextView=view.findViewById(R.id.txtPhoneNumber)
        userName.text=sharedPreferences.getString("user_name",null)
        val phone="+91-${sharedPreferences.getString("user_mobile_number",null)}"
        userPhone.text=phone
        navigationView.addHeaderView(view)

        val actionBarDrawerToggle=ActionBarDrawerToggle(this@NewActivity,drawerLayout,
            R.string.open_drawer,R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem!=null){
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it

            when(it.itemId){
                R.id.home ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,HomeFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="All Restaurants"
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,ProfileFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="My Profile"
                }

                R.id.favourites ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,FavouriteFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Favourite Restaurants"
                }
                R.id.orderHistory ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,OrderHistoryFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="My Previous Orders"
                }
                R.id.faq ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,FaqFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Frequently Asked Questions"
                }

                R.id.logout ->{
                    val builder=AlertDialog.Builder(this@NewActivity)
                    builder.setTitle("Confirmation")
                    builder.setMessage("Are you sure you want to logout?")
                    builder.setPositiveButton("Yes"){dialogInterface,which->
                        val intent=Intent(this@NewActivity,MainActivity::class.java)
                        val editor:SharedPreferences.Editor=sharedPreferences.edit()
                        editor.clear()
                        editor.apply()
                        val alertDialog:AlertDialog=builder.create()
                        alertDialog.dismiss()
                        startActivity(intent)
                    finish()}

                    builder.setNeutralButton("Cancel"){dialogInterface,which->
                        Toast.makeText(applicationContext,"Clicked cancel",Toast.LENGTH_SHORT).show()
                        drawerLayout.closeDrawers()
                    }
                    builder.setNegativeButton("No"){dialogInterface,which->
                        Toast.makeText(applicationContext,"Clicked No",Toast.LENGTH_SHORT).show()
                        drawerLayout.closeDrawers()
                    }
                    val alertDialog:AlertDialog=builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
            }
            return@setNavigationItemSelectedListener(true)
        }
    }
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
   override fun onOptionsItemSelected(item: MenuItem):Boolean{
       val id=item.itemId
       if(id== android.R.id.home){
           drawerLayout.openDrawer(GravityCompat.START)
       }
       return  super.onOptionsItemSelected(item)
   }
    fun openHome(){
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout,HomeFragment()).commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }
}