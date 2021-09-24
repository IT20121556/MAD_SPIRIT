package com.example.shppingapp.modal;

public class Cart {
    private String key,name,price,image;
    private int qty;
    private float toatalPrice;

    public Cart() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getToatalPrice() {
        return toatalPrice;
    }

    public void setToatalPrice(float toatalPrice) {
        this.toatalPrice = toatalPrice;
    }
}
