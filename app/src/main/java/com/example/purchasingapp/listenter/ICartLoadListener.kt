package com.example.purchasingapp.listenter

import com.example.purchasingapp.model.CartModel

interface ICartLoadListener {
    fun onLoadCartSuccess(cartModeList: List<CartModel>)
    fun onLoadCartFailed(message: String?)
}