package com.example.purchasingapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purchasingapp.R
import com.example.purchasingapp.model.ClothesModel
import java.lang.StringBuilder

class MyClothesAdapter(
    private val context: Context,
    private val list: List<ClothesModel>
): RecyclerView.Adapter<MyClothesAdapter.MyClothesViewHolder>(){

    class MyClothesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageView:ImageView?=null
        var txtName: TextView?=null
        var txtPrice: TextView?=null

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClothesViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyClothesViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder().append(list[position].price)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}