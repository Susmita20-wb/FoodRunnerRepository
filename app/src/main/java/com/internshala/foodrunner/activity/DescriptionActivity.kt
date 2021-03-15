package com.internshala.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*

import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.DescriptionRecyclerAdapter
import com.internshala.foodrunner.adapter.GetFavAsyncTask
import com.internshala.foodrunner.adapter.HomeRecyclerAdapter
import com.internshala.foodrunner.database.OrderEntity
import com.internshala.foodrunner.database.RestaurantDatabase
import com.internshala.foodrunner.model.Description
import com.internshala.foodrunner.util.ConnectionManager
import com.internshala.foodrunner.util.SessionManager
import kotlin.collections.HashMap

class DescriptionActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var imgFav:ImageButton
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var coordinateLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var btnGoToCart:Button

    val dishInfoList= arrayListOf<Description>()
    val orderList= arrayListOf<Description>()

    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerAdapter: DescriptionRecyclerAdapter
    lateinit var progressLayout:RelativeLayout
    lateinit var restaurantName:String
    lateinit var sessionManager: SessionManager

    var restaurantId:String="100"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        sessionManager= SessionManager(this@DescriptionActivity)
        sharedPreferences=getSharedPreferences(sessionManager.PREF_NAME, Context.MODE_PRIVATE)
        recyclerView=findViewById(R.id.recyclerView)

        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE
        btnGoToCart=findViewById(R.id.btnGoToCart)
        coordinateLayout=findViewById(R.id.coordinateLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        imgFav=findViewById(R.id.imgFav)

        btnGoToCart.setOnClickListener{
            proceedToCart()
        }

        layoutManager=LinearLayoutManager(this@DescriptionActivity)

        if(intent!=null){
            restaurantId= intent.getStringExtra("restaurant_id") as String
            restaurantName=intent.getStringExtra("restaurant_name") as String
            val listOfFav= GetFavAsyncTask(this).execute().get()
            if(listOfFav.isNotEmpty() && listOfFav.contains(restaurantId)){
                imgFav.setBackgroundResource(R.drawable.fav)
            }
            else{
                imgFav.setBackgroundResource(R.drawable.fav2)
            }
        }
        else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occured!",Toast.LENGTH_SHORT).show()
        }

        if(restaurantId=="100"){
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occured!",Toast.LENGTH_SHORT).show()
        }
        setUpToolbar(restaurantName)

        val queue=Volley.newRequestQueue(this@DescriptionActivity)

        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonRequest = object : JsonObjectRequest
                (
                Method.GET,
                "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId",
                null,
                Response.Listener {
                    progressLayout.visibility = View.GONE
                    try {
                        val obj2 = it.getJSONObject("data")
                        val success = obj2.getBoolean("success")
                        if (success) {
                            val data = obj2.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val dishJsonObject = data.getJSONObject(i)
                                val dishObject = Description(
                                    dishJsonObject.getString("id"),
                                    dishJsonObject.getString("name"),
                                    dishJsonObject.getString("cost_for_one")
                                )
                                dishInfoList.add(dishObject)
                                recyclerAdapter =
                                    DescriptionRecyclerAdapter(this@DescriptionActivity,
                                        dishInfoList,
                                        object : DescriptionRecyclerAdapter.OnItemClickListener {
                                            override fun onAddItemClick(dishObject: Description) {
                                                orderList.add(dishObject)
                                                if (orderList.size > 0) {
                                                    btnGoToCart.visibility = View.VISIBLE
                                                    DescriptionRecyclerAdapter.isCartEmpty = false
                                                }
                                            }

                                            override fun onRemoveItemClick(dishObject: Description) {
                                                orderList.remove(dishObject)
                                                if (orderList.isEmpty()) {
                                                    btnGoToCart.visibility = View.GONE
                                                    DescriptionRecyclerAdapter.isCartEmpty = true
                                                }
                                            }
                                        })

                                recyclerView.adapter = recyclerAdapter
                                recyclerView.itemAnimator = DefaultItemAnimator()
                                recyclerView.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some error occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some error occured $e",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this@DescriptionActivity, "Volley error ", Toast.LENGTH_SHORT)
                        .show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9326ad19a377d6"
                    return headers
                }
            }

                queue.add(jsonRequest)
            }
            else{
                val dialog = AlertDialog.Builder(this@DescriptionActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@DescriptionActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    fun proceedToCart(){

        val foodItems:String= Gson().toJson(orderList)
        val async=CartItems(this@DescriptionActivity,restaurantId.toString(),foodItems,1).execute()
        val result=async.get()
        if (result){
            val intent=Intent(this@DescriptionActivity,CartActivity::class.java)
            intent.putExtra("resId",restaurantId)
            intent.putExtra("resName",restaurantName)
            startActivity(intent)
        }
        else{
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occured",Toast.LENGTH_SHORT).show()
        }
    }
    class CartItems(context:Context,val restaurantId:String,val foodItems:String,val mode:Int):
        AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode){
                1->{
                    db.orderDao().insertOrder(OrderEntity(restaurantId,foodItems))
                    db.close()
                    return true
                }
                2->{
                    db.orderDao().deleteOrder(OrderEntity(restaurantId,foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    override fun onBackPressed() {
        if(orderList.size> 0){
            val alertDialog=androidx.appcompat.app.AlertDialog.Builder(this)
            alertDialog.setTitle("Alert")
            alertDialog.setMessage("Going back will remove everything from cart")
            alertDialog.setPositiveButton("Okay"){text,listener->
                super.onBackPressed()
                }
            alertDialog.setNegativeButton("No"){text,listener->

            }
            alertDialog.show()
        }
        else{
            super.onBackPressed()
        }
    }

    fun setUpToolbar(name:String){
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title=name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}