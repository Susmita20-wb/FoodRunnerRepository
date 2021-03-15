package com.internshala.foodrunner.adapter

    import android.content.Context
    import android.content.Intent
    import android.os.AsyncTask
    import android.os.Build
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.*
    import androidx.appcompat.app.AppCompatActivity
    import androidx.cardview.widget.CardView
    import androidx.fragment.app.FragmentActivity
    import androidx.recyclerview.widget.RecyclerView
    import androidx.room.Room
    import com.internshala.foodrunner.R
    import com.internshala.foodrunner.activity.CartActivity
    import com.internshala.foodrunner.activity.DescriptionActivity
    import com.internshala.foodrunner.database.RestaurantDatabase
    import com.internshala.foodrunner.database.RestaurantEntity
    import com.internshala.foodrunner.model.Restaurant
    import com.squareup.picasso.Picasso

    class HomeRecyclerAdapter  (val context: Context, var itemList:ArrayList<Restaurant>):
        RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

        class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
            val txtPrice: TextView = view.findViewById(R.id.txtPrice)
            val txtRating: TextView = view.findViewById(R.id.txtRating)
            val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
            val imgFav: ImageButton = view.findViewById(R.id.imgFav)
            val cardRestaurant: CardView = view.findViewById(R.id.cardRestaurant)

            val rlContent: RelativeLayout = view.findViewById(R.id.rlContent)
        }

        val button: Boolean = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row, parent, false)

            return HomeViewHolder(view)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
            val restaurant = itemList[position]

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.imgRestaurantImage.clipToOutline = true
            }

            holder.txtRestaurantName.text = restaurant.restaurantName
            holder.txtRating.text = restaurant.restaurantRating
            holder.txtPrice.text = restaurant.restaurantCost_For_One

            Picasso.get().load(restaurant.restaurantImage).error(R.drawable.default_restaurant_image).into(holder.imgRestaurantImage)


            val listOfFav = GetFavAsyncTask(context).execute().get()

            if (listOfFav.isNotEmpty() && listOfFav.contains(restaurant.restaurantId.toString())) {
                holder.imgFav.setBackgroundResource(R.drawable.fav)

            } else {
                holder.imgFav.setBackgroundResource(R.drawable.fav2)
            }

            holder.imgFav.setOnClickListener {
                val restaurantEntity = RestaurantEntity(
                        restaurant.restaurantId.toInt(),
                        restaurant.restaurantName,
                        restaurant.restaurantRating,
                        restaurant.restaurantCost_For_One,
                        restaurant.restaurantImage
                )
                if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                    val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                    val data = async.get()
                    if (data) {
                        Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show()
                        holder.imgFav.setBackgroundResource(R.drawable.fav)
                    }
                } else {
                    val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                    val data = async.get()
                    if (data) {
                        Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show()
                        holder.imgFav.setBackgroundResource(R.drawable.fav2)
                    }
                }
            }

            holder.rlContent.setOnClickListener {
                val intent = Intent(context, DescriptionActivity::class.java)
                intent.putExtra("restaurant_id", restaurant.restaurantId)
                intent.putExtra("restaurant_name", restaurant.restaurantName)
                context.startActivity(intent)

            }
        }

        fun filterList(filteredList: ArrayList<Restaurant>) {
            itemList = filteredList
            notifyDataSetChanged()
        }
    }

    class GetFavAsyncTask(context: Context): AsyncTask<Void, Void, List<String>>(){
        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant-db").build()

        override fun doInBackground(vararg params: Void?): List<String> {
            val list=db.restaurantDao().getAllRestaurants()
            val listOfIds= arrayListOf<String>()
            for (i in list){
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }}

    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntity, val mode:Int):
        AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    val res: RestaurantEntity?=db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return res!=null
                }
                2->{
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3->{
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }


