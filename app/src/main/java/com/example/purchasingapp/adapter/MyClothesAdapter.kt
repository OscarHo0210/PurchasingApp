package com.example.purchasingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purchasingapp.R
import com.example.purchasingapp.eventbus.UpdateCartEvent
import com.example.purchasingapp.listenter.ICartLoadListener
import com.example.purchasingapp.listenter.IRecyclerClickListener
import com.example.purchasingapp.model.CartModel
import com.example.purchasingapp.model.ClothesModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class MyClothesAdapter(
    private val context: Context,
    private val list: List<ClothesModel>,
            private val cartListener: ICartLoadListener
): RecyclerView.Adapter<MyClothesAdapter.MyClothesViewHolder>(){

    class MyClothesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageView:ImageView?=null
        var txtName: TextView?=null
        var txtPrice: TextView?=null

        private var clickListener:IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener) {
            this.clickListener = clickListener;
        }

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView

            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            clickListener!!.onItemClickListener(p0,adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClothesViewHolder {
        return MyClothesViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_clothes_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyClothesViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder().append(list[position].price)

        holder.setClickListener(object :IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(list[position])
            }

        })
    }

    private fun addToCart(clothesModel: ClothesModel) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(clothesModel.key!!)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String,Any> = HashMap()
                        cartModel!!.quantity = cartModel!!.quantity+1;
                        updateData["quantity"] = cartModel!!.quantity;
                        updateData["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()

                        userCart.child(clothesModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("成功加入購物車！")
                            }
                            .addOnFailureListener{e-> cartListener.onLoadCartFailed(e.message)}
                    }else{
                        val cartModel = CartModel()
                        cartModel.key = clothesModel.key
                        cartModel.name = clothesModel.name
                        cartModel.image = clothesModel.image
                        cartModel.price = clothesModel.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = clothesModel.price!!.toFloat()

                        userCart.child(clothesModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("成功加入購物車！")
                            }
                            .addOnFailureListener{e-> cartListener.onLoadCartFailed(e.message)}

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }

            })
    }

    override fun getItemCount(): Int {
        return list.size
    }

}