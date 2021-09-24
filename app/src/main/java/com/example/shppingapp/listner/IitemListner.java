package com.example.shppingapp.listner;

import com.example.shppingapp.modal.Item;

import java.util.List;

public interface IitemListner {
    void onItemLoadSuccess(List<Item> items);
    void onItemLoadFail(String message);
}
