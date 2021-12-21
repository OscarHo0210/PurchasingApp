package com.example.purchasingapp.listenter

import com.example.purchasingapp.model.ClothesModel

interface ClothesLoadListener {
    fun onClothesLoadSuccess(clothesModelList: List<ClothesModel>?)
    fun onClothesLoadSuccess(message:String?)
    fun onClothesLoadFailed(message: String?)
}