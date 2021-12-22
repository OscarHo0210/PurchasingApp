package com.example.purchasingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.purchasingapp.adapter.MyClothesAdapter
import com.example.purchasingapp.eventbus.UpdateCartEvent
import com.example.purchasingapp.listenter.ClothesLoadListener
import com.example.purchasingapp.listenter.ICartLoadListener
import com.example.purchasingapp.model.CartModel
import com.example.purchasingapp.model.ClothesModel
import com.example.purchasingapp.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

class MainActivity : AppCompatActivity(), ClothesLoadListener, ICartLoadListener {
    lateinit var clothesLoadListener: ClothesLoadListener
    lateinit var cartLoadListener: ICartLoadListener

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public fun onUpdateCartEvent(event:UpdateCartEvent){
        countCartFromFirebase()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        loadClothesFromFirebase()
        countCartFromFirebase()
    }

    private fun countCartFromFirebase() {
        val cartModel: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children){
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModel.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModel)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

    private fun loadClothesFromFirebase() {
        val clothesModels: MutableList<ClothesModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Clothes")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        for (clothesSnapshot in snapshot.children){
                            val clothesModel = clothesSnapshot.getValue(ClothesModel::class.java)
                            clothesModel!!.key = clothesSnapshot.key
                            clothesModels.add(clothesModel)
                        }
                        clothesLoadListener.onClothesLoadSuccess(clothesModels)
                    }else
                        clothesLoadListener.onClothesLoadFailed("服裝不存在")
                }

                override fun onCancelled(error: DatabaseError) {
                    clothesLoadListener.onClothesLoadFailed(error.message)
                }
            })
    }

    private fun init() {
        clothesLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_clothes.layoutManager = gridLayoutManager
        recycler_clothes.addItemDecoration(SpaceItemDecoration())
    }

    override fun onClothesLoadSuccess(clothesModelList: List<ClothesModel>?) {
        val adapter = MyClothesAdapter(this,clothesModelList!!,cartLoadListener)
        recycler_clothes.adapter = adapter
    }

    override fun onClothesLoadSuccess(message: String?) {
        TODO("Not yet implemented")
    }

    override fun onClothesLoadFailed(message: String?) {
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadCartSuccess(cartModeList: List<CartModel>) {
        var cartSum = 0
        for (cartModel in cartModeList!!) cartSum+= cartModel!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }
}