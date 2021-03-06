package com.internshala.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.model.Description
import kotlinx.android.synthetic.main.recycler_cart_single_row.view.*

class CartRecyclerAdapter(val cartArray:ArrayList<Description>,
     val context:Context):
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>(){

    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtDishName:TextView=view.findViewById(R.id.txtDishName)
        val txtCost:TextView=view.findViewById(R.id.txtCost)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int
    ): CartRecyclerAdapter.CartViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartArray.size
    }

    override fun onBindViewHolder(holder: CartRecyclerAdapter.CartViewHolder, position: Int) {
        val cartObj=cartArray[position]
        holder.txtDishName.text=cartObj.dishName
        val price="Rs. ${cartObj.dishPrice}"
        holder.txtCost.text=price
    }
}