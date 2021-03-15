package com.internshala.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.HomeRecyclerAdapter
import com.internshala.foodrunner.model.OrderDetails
import com.internshala.foodrunner.model.Restaurant
import com.internshala.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.recycler_home_single_row.*
import org.json.JSONException
import java.util.*
import java.util.Arrays.sort
import java.util.Collections.sort
import kotlin.Comparator
import kotlin.collections.HashMap

class  HomeFragment:Fragment() {
    lateinit var recyclerView:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var restaurantInfoList= arrayListOf<Restaurant>()
    lateinit var editTextSearch:EditText
    lateinit var recyclerAdapter:HomeRecyclerAdapter
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar
    var checkedItem:Int=-1
    lateinit var dashboard_fragment_cant_find_restaurant:RelativeLayout

    var ratingComparator=Comparator<Restaurant>{restaurant1,restaurant2 ->
        if (restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true) == 0){
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
        }

        else{
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true)
        }
    }
    var costComparator= Comparator<Restaurant>{rest1,rest2 ->
        rest1.restaurantCost_For_One.compareTo(rest2.restaurantCost_For_One,true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view=inflater.inflate(R.layout.fragment_home, container,false)

        recyclerView=view.findViewById(R.id.recyclerView) as RecyclerView
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        editTextSearch=view.findViewById(R.id.etSearch)
        dashboard_fragment_cant_find_restaurant=view.findViewById(R.id.dashboard_fragment_cant_find_restaurant)

        progressBar.visibility=View.VISIBLE
        layoutManager=LinearLayoutManager(activity)

        editTextSearch.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                filterFun(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest=object :JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                try{
                    progressLayout.visibility=View.GONE
                    progressBar.visibility=View.GONE
                    val obj=it.getJSONObject("data")
                    val success=obj.getBoolean("success")
                    if(success) {
                        val data = obj.getJSONArray("data")


                        for (i in 0 until data.length()) {
                            val restaurantJsonObject = data.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantObject)
                            recyclerAdapter =
                                HomeRecyclerAdapter(activity as Context, restaurantInfoList)
                            recyclerView.adapter = recyclerAdapter
                            recyclerView.layoutManager = layoutManager
                        }
                    }
                    else{
                        Toast.makeText(activity as Context,"Some Error Occured",Toast.LENGTH_SHORT).show()
                    }}
                catch (e:JSONException){
                    e.printStackTrace()
                }
                },Response.ErrorListener {
                Toast.makeText(activity as Context,"Volley Error Occured",Toast.LENGTH_SHORT).show()
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
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not found")
            dialog.setPositiveButton("Open Settings"){text ,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }
        return view

    }
    fun filterFun(s:String){
        val filteredList= arrayListOf<Restaurant>()
        for(item in restaurantInfoList){
            if(item.restaurantName.toLowerCase().contains(s.toLowerCase())){
                filteredList.add(item)
            }
        }
        if(filteredList.size==0){
            dashboard_fragment_cant_find_restaurant.visibility=View.VISIBLE
        }
        else{
            dashboard_fragment_cant_find_restaurant.visibility=View.INVISIBLE
        }
        recyclerAdapter.filterList(filteredList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.action_sort) {
            Collections.sort(restaurantInfoList, ratingComparator)
            restaurantInfoList.reverse()
            recyclerAdapter.notifyDataSetChanged()
        }

        return super.onOptionsItemSelected(item)
    }

}