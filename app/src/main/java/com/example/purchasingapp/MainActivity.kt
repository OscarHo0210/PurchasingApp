package com.example.purchasingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.purchasingapp.adapter.MyClothesAdapter
import com.example.purchasingapp.listenter.ClothesLoadListener
import com.example.purchasingapp.model.ClothesModel
import com.example.purchasingapp.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity(), ClothesLoadListener {
    lateinit var clothesLoadListener: ClothesLoadListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        loadClothesFromFirebase()
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
        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_clothes.layoutManager = gridLayoutManager
        recycler_clothes.addItemDecoration(SpaceItemDecoration())
    }

    override fun onClothesLoadSuccess(clothesModelList: List<ClothesModel>?) {
        val adapter = MyClothesAdapter(this,clothesModelList!!)
        recycler_clothes.adapter = adapter
    }

    override fun onClothesLoadSuccess(message: String?) {
        TODO("Not yet implemented")
    }

    override fun onClothesLoadFailed(message: String?) {
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }
}