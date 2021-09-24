package com.example.shppingapp.listner;

import com.example.shppingapp.modal.Cart;

import java.util.List;

public interface IcartListner {

    void onCartLoadSuccess(List<Cart> cart);
    void onCartLoadFail(String message);
}
