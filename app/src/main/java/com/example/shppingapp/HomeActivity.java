package com.example.shppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.shppingapp.adapter.MyItemAdapter;
import com.example.shppingapp.eventBus.MyUpdateCartEvent;
import com.example.shppingapp.listner.IcartListner;
import com.example.shppingapp.listner.IitemListner;
import com.example.shppingapp.modal.Cart;
import com.example.shppingapp.modal.Item;
import com.example.shppingapp.util.LoadingDialog;
import com.example.shppingapp.util.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements IitemListner, IcartListner {
    @BindView(R.id.recycle_item)
    RecyclerView recycle_item;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;
    @BindView(R.id.navHomeBtn)
    Button navHomeBtn;
    @BindView(R.id.navAccountBtn)
    Button navAccountBtn;
    @BindView(R.id.navItemBtn)
    Button navItemBtn;
    @BindView(R.id.navShippingBtn)
    Button navShippingBtn;
    @BindView(R.id.showMoreBtn)
    Button showMoreBtn;

    IitemListner iitemListner;
    IcartListner icartListner;
    LoadingDialog loadingDialog;
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event){
        countCartItems();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadingDialog = new LoadingDialog(HomeActivity.this);
        init();
        navigater();
        loadItemFromFirebase();
        countCartItems();

        loadingDialog.startLoadingDialog();
    }

    private void navigater(){
        navHomeBtn.setOnClickListener(v -> startActivity(new Intent(this,HomeActivity.class)));
        navItemBtn.setOnClickListener(v -> startActivity(new Intent(this,MainActivity.class)));
        showMoreBtn.setOnClickListener(v -> startActivity(new Intent(this,MainActivity.class)));

    }
    private void init(){
        ButterKnife.bind(this);

        iitemListner = this;
        icartListner =this;

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycle_item.setLayoutManager(horizontalLayoutManagaer);
        recycle_item.addItemDecoration(new SpaceItemDecoration());

        btnCart.setOnClickListener(v-> startActivity(new Intent(this,CartActivity.class)));
    }
    private void loadItemFromFirebase() {
        List<Item> items = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Item")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                                Item item = itemSnapshot.getValue(Item.class);
                                item.setKey(itemSnapshot.getKey());
                                items.add(item);
                            }
                            iitemListner.onItemLoadSuccess(items);
                            loadingDialog.dismissDialog();
                        }else{
                            iitemListner.onItemLoadFail("Cant find items");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iitemListner.onItemLoadFail(error.getMessage());
                    }
                });
    }
    private void countCartItems() {
        List<Cart> cartModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Cart").child("001")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()){
                            Cart cartModel = cartSnapshot.getValue(Cart.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        icartListner.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        icartListner.onCartLoadFail(error.getMessage());
                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();
        countCartItems();
    }

    @Override
    public void onItemLoadSuccess(List<Item> items) {
        MyItemAdapter adapter = new MyItemAdapter(this,items,icartListner);
        recycle_item.setAdapter(adapter);
    }

    @Override
    public void onItemLoadFail(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<Cart> cart) {
        int cartSum = 0;
        for(Cart cartModal : cart)
            cartSum += cartModal.getQty();

        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFail(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }
}