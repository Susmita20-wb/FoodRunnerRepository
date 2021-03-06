package com.internshala.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.HomeRecyclerAdapter
import com.internshala.foodrunner.database.RestaurantDatabase
import com.internshala.foodrunner.database.RestaurantEntity
import com.internshala.foodrunner.model.Restaurant

class FavouriteFragment: Fragment() {

    lateinit var recyclerFav: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var rlFav: RelativeLayout
    lateinit var rlNoFav: RelativeLayout
    lateinit var recyclerAdapter: HomeRecyclerAdapter

    var dbResList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerFav = view.findViewById(R.id.recyclerFav)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        rlFav = view.findViewById(R.id.rlFav)
        rlNoFav = view.findViewById(R.id.rlNoFav)

        progressLayout.visibility = View.VISIBLE
        setUpRecycler(view)

        return view
    }

    fun setUpRecycler(view: View) {
        recyclerFav = view.findViewById(R.id.recyclerFav)
         val resList = FavouriteAsync(activity as Context).execute().get()
        if (resList.isEmpty()) {
            progressLayout.visibility = View.GONE
            rlFav.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE
        } else {
            progressLayout.visibility = View.GONE
            rlFav.visibility = View.VISIBLE
            rlNoFav.visibility = View.GONE

            for (i in resList) {
                dbResList.add(
                    Restaurant(
                        i.id.toString(),
                        i.name,
                        i.rating,
                        i.costForTwo,
                        i.imageUrl
                    )
                )
            }

            recyclerAdapter = HomeRecyclerAdapter(activity as Context,dbResList)
            var layoutManager = LinearLayoutManager(activity)
            recyclerFav.layoutManager = layoutManager
            recyclerFav.itemAnimator = DefaultItemAnimator()
            recyclerFav.adapter = recyclerAdapter
            recyclerFav.setHasFixedSize(true)
        }
    }

    class FavouriteAsync(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>() {
        val db =
                Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {

            return db.restaurantDao().getAllRestaurants()
        }
    }
}